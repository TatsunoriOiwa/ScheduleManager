package qwertzite.schedulemanager.schedule;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import qwertzite.schedulemanager.Log;
import qwertzite.schedulemanager.util.TimeDateUtil;
import qwertzite.schedulemanager.util.datetime.YearMonth;

public class Schedules {
	
	public static final String FILE_LOCATION = "schedule.json";
	public static final String DONE_DIR = "records";
	
	private List<ScheduleEntry> entries = new ArrayList<>();
	private List<ScheduleEntry> records = new ArrayList<>();
//	private List<ScheduleEntry> recordLatestMonth = new ArrayList<>();
	@SuppressWarnings("unused")
	private YearMonth oldest;
	
	public Schedules() {}
	
	public int updatePosition(ScheduleEntry selected, boolean exec) {
		if (selected.isOutOfOrder()) return -1;
		int initial = this.entries.indexOf(selected);
		
		boolean inInvLayer = this.isInInvLayer(selected);
		System.out.println("inv=" + inInvLayer);
		
		int pos;
		if (inInvLayer) {
			pos = -1;
		} else {
			int fore = this.scanForeward(selected);
			int back = this.scanBackward(selected);
			System.out.println("fore=" + fore + ",back=" + back);
			if (fore == initial && back == initial) {
				pos = -1;
			} else if (fore != initial && back == initial) {
				pos = fore;
			} else if (back != initial && fore == initial) {
				pos = back;
			} else {
				pos = -1;
				Log.warn("Auto Entry Move: Both back and fore is available! fore={}, back={}", fore, back);
			}
		}
		System.out.println("pos=" + pos);
		if (exec && pos >= 0) {
			this.entries.remove(selected);
			this.entries.add(pos, selected);
		}
		return pos;
	}
	
	private boolean isInInvLayer(ScheduleEntry selected) {
		int initial = this.entries.indexOf(selected);
		if (initial == 0 || initial == this.entries.size() -1) return false;
		LocalDateTime earliest = this.entries.get(initial-1).earliestPossible();
		LocalDateTime latest = this.entries.get(initial+1).latestPossible();
		for (int i = initial -1; i >= 0; i--) {
			LocalDateTime cmp = this.entries.get(i).earliestPossible();
			if (cmp.isAfter(earliest)) earliest = cmp;
			else if (cmp.isBefore(earliest)) {
				System.out.println("fore i = " + (i+1));
				break;
			}
		}
		for (int i = initial + 1; i < this.entries.size(); i++) {
			LocalDateTime cmp = this.entries.get(i).latestPossible();
			if (cmp.isBefore(latest)) latest = cmp;
			else if (cmp.isAfter(latest)) {
				System.out.println("back i = " + (i-1));
				break;
			}
		}
		return earliest.isAfter(latest);
	}
	
	private int scanForeward(ScheduleEntry selected) {
		int init = this.entries.indexOf(selected);
		if (init == 0) return init;
		int pos = init;
		
		LocalDateTime latest = this.entries.get(init - 1).latestPossible();
		for (int i = init - 1; i >= 0; i--) {
			ScheduleEntry entry = this.entries.get(i);
			int cmp = selected.compare(entry);
			if (cmp > 0 || entry.earliestPossible().isAfter(latest)) { // 反転の検出
				break;
			} else if (cmp < 0) {
				pos = i;
			}
			LocalDateTime tmp = entry.latestPossible();
			if (tmp.isBefore(latest)) { latest = tmp; }
		}
		return pos;
	}
	
	private int scanBackward(ScheduleEntry selected) {
		int init = this.entries.indexOf(selected);
		if (init == this.entries.size() -1) return init;
		int pos = init;
		
		LocalDateTime earliest = this.entries.get(init + 1).earliestPossible();
		for (int i = init + 1; i < this.entries.size(); i++) {
			ScheduleEntry entry = this.entries.get(i);
			int cmp = selected.compare(entry);
			if (cmp < 0 || entry.latestPossible().isBefore(earliest)) {
				break;
			} else if (cmp > 0) {
				pos = i;
			}
			LocalDateTime tmp = entry.earliestPossible();
			if (tmp.isAfter(earliest)) { earliest = tmp; }
		}
		return pos;
	}
	
	public int insertNewSchedule(ScheduleEntry old, ScheduleEntry n) {
		int pos = this.entries.indexOf(old);
		if (pos < 0) {
			pos = 0;
		} else {
			pos += 1;
		}
		this.entries.add(pos, n);
		return pos;
	}
	
	public void deleteSchedule(ScheduleEntry entry) {
		this.entries.remove(entry);
	}
	
	public void markScheduleAsDone(ScheduleEntry entry) {
		entry.setCompleted();
		this.entries.remove(entry);
//		this.recordLatestMonth.add(0, entry);
		this.records.add(0, entry);
	}
	
	public int moveEntryUpDown(ScheduleEntry entry, boolean up) {
		int pos = this.entries.indexOf(entry);
		this.entries.remove(entry);
		pos += up ? -1 : 1;
		this.entries.add(pos, entry);
		if (this.updatePosition(entry, false) != -1) {
			entry.setOutOfOrder(true);
			System.out.println("out of order!");
		}
		return pos;
	}
	
	public boolean loadSchedule(JsonObject json) {
		this.entries.clear();
		JsonArray array = json.getAsJsonArray("schedule");
		for (JsonElement e : array) {
			if (!e.isJsonObject()) {
				Log.warn("failed to load an entry {}", e);
				continue;
			}
			JsonObject eobj = e.getAsJsonObject();
			this.entries.add(new ScheduleEntry().loadEntryFromJsonObj(eobj));
		}
		return true;
	}
	
	public List<ScheduleEntry> loadDoneTask(JsonObject json, YearMonth now, boolean clear) {
		List<ScheduleEntry> list = new ArrayList<>();
		
		JsonArray array = json.getAsJsonArray("schedule");
		for (JsonElement e : array) {
			if (!e.isJsonObject()) {
				Log.warn("failed to load an entry {}", e);
				continue;
			}
			JsonObject eobj = e.getAsJsonObject();
			list.add(new ScheduleEntry().loadEntryFromJsonObj(eobj));
		}
		if (clear) this.records.clear();
		this.records.addAll(list);
		this.oldest = now;
		return list;
	}
	
	public void saveSchedules(JsonObject json) {
		JsonArray array = new JsonArray();
		json.add("schedule", array);
		for (ScheduleEntry e : this.getEntries()) {
			array.add(e.toJsonObj());
		}
	}
	
	public void saveRecord(JsonObject json) {
		JsonArray array = new JsonArray();
		json.add("schedule", array);
		for (ScheduleEntry e : this.records) {
			if (e.getCompletedTime().getMonth().getValue() == TimeDateUtil.getCurrentMonth().getIndex()) {
				array.add(e.toJsonObj());
			}
		}
	}
	
	public List<ScheduleEntry> getEntries() {
		return this.entries;
	}
	
	public List<ScheduleEntry> getRecords() {
		return this.records;
	}
	
	public int getIndexOf(ScheduleEntry entry) {
		return this.entries.indexOf(entry);
	}
	
	public int getEntryNum() { return this.entries.size(); }
}
