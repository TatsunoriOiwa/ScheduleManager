package qwertzite.schedulemanager.schedule;

import java.time.LocalDateTime;

import com.google.gson.JsonObject;

import qwertzite.schedulemanager.tag.EnumIsPending;
import qwertzite.schedulemanager.tag.EnumIsPlaceHolder;
import qwertzite.schedulemanager.tag.EnumPublicness;
import qwertzite.schedulemanager.tag.EnumScheduleType;
import qwertzite.schedulemanager.tag.EnumUrgent;
import qwertzite.schedulemanager.util.FuzzyDate;
import qwertzite.schedulemanager.util.TimeDateUtil;
import qwertzite.schedulemanager.util.datetime.EnumMonth;

public class ScheduleEntry implements Cloneable {
	
	private FuzzyDate scheduleDate = new FuzzyDate();
	private String title = "";
	private String note = "";
	private LocalDateTime completedTimeStamp;
	
	private EnumPublicness tagPublicness = EnumPublicness.PUBLIC;
	private EnumScheduleType tagScheduleType = EnumScheduleType.TASK;
	private EnumIsPlaceHolder tagPlaceHolder = EnumIsPlaceHolder.FALSE;
	private EnumIsPending tagPending = EnumIsPending.FALSE;
	private EnumUrgent tagUrgent = EnumUrgent.NORMAL;
	
	private boolean outofOrder = false;
	
	public ScheduleEntry() {}
	
	public ScheduleEntry loadEntryFromJsonObj(JsonObject eobj) {
		if (eobj.has("name")) { this.setTitle(eobj.get("name").getAsString()); }
		this.scheduleDate = new FuzzyDate().loadFromJsonObj(eobj);
		if (eobj.has("note")) { this.setNote(eobj.get("note").getAsString()); }
		if (eobj.has("completed")) { this.completedTimeStamp = LocalDateTime.parse(eobj.get("completed").getAsString()); }
		if (eobj.has("tagPublicness"))  { this.setTagPublicness(EnumPublicness.fromIndex(eobj.get("tagPublicness").getAsInt())); }
		if (eobj.has("tagScheduleType")){ this.setTagScheduleType(EnumScheduleType.fromIndex(eobj.get("tagScheduleType").getAsInt())); }
		if (eobj.has("tagPlaceholder")) { this.setTagPlaceHolder(EnumIsPlaceHolder.fromIndex(eobj.get("tagPlaceholder").getAsInt())); }
		if (eobj.has("tagPending"))     { this.setTagPending(EnumIsPending.fromIndex(eobj.get("tagPending").getAsInt())); }
		if (eobj.has("tagUrgent"))      { this.setTagUrgent(EnumUrgent.fromIndex(eobj.get("tagUrgent").getAsInt())); }
		if (eobj.has("outOfOrder")) { this.setOutOfOrder(eobj.get("outOfOrder").getAsBoolean()); }
		return this;
	}
	
	public JsonObject toJsonObj() {
		JsonObject obj = new JsonObject();
		obj.addProperty("name", this.getTitle());
		obj.addProperty("year", this.getYear());
		obj.addProperty("month", this.getMonth().getIndex());
		obj.addProperty("day", this.getDay());
		obj.addProperty("time", this.getTime());
		obj.addProperty("note", this.getNote());
		if (this.completedTimeStamp != null) { obj.addProperty("completed", this.getCompletedTime().toString()); }
		obj.addProperty("tagPublicness", this.getTagPublicness().getIndex());
		obj.addProperty("tagScheduleType", this.getTagScheduleType().getIndex());
		obj.addProperty("tagPlaceholder", this.getTagPlaceHolder().getIndex());
		obj.addProperty("tagPending", this.getTagPending().getIndex());
		obj.addProperty("tagUrgent", this.getTagUrgent().getIndex());
		obj.addProperty("outOfOrder", this.isOutOfOrder());
		return obj;
	}
	
	/**
	 * 
	 * @param other
	 * @return -1 if this earlier than the other, 1 if this > other.
	 */
	public int compare(ScheduleEntry other) {
		if (this.isOutOfOrder() || other.isOutOfOrder()) return 0;
		return this.getDate().compare(other.getDate());
	}
	
	public int isStrictThan(ScheduleEntry other) {
		if (this.isOutOfOrder()) return other.isOutOfOrder() ? 0 : -1;
		if (other.isOutOfOrder()) return 1;
		return this.getDate().isStrictThan(other.getDate());
	}

	public boolean hasYear() { return this.scheduleDate.hasYear(); }
	public int getYear() { return this.scheduleDate.getYear(); }
	public boolean setYear(int year) { return this.scheduleDate.setYear(year); }
	public void clearYear() { this.scheduleDate.clearYear(); }

	public boolean hasMonth() { return this.scheduleDate.hasMonth(); }
	public EnumMonth getMonth() { return this.scheduleDate.getMonth(); }
	public boolean setMonth(EnumMonth month) { return this.scheduleDate.setMonth(month); }
	public void clearMonth() { this.scheduleDate.clearMonth(); }
	
	public boolean hasDay() { return this.scheduleDate.hasDay(); }
	public int getDay() { return this.scheduleDate.getDay(); }
	public boolean setDay(int day) { return this.scheduleDate.setDay(day); }
	public void clearDay() { this.scheduleDate.clearDay(); }
	
	public String getDayOfWeekString() { return this.scheduleDate.getDayOfWeekString(); }
	
	public boolean hasTime() { return this.scheduleDate.hasTime(); }
	public int getTime() { return this.scheduleDate.getTime(); }
	public boolean setTime(int time) { return this.scheduleDate.setTime(time); }
	public void clearTime() { this.scheduleDate.clearTime(); }
	
	public FuzzyDate getDate() { return this.scheduleDate; }
	
	public LocalDateTime earliestPossible() { return this.isOutOfOrder() ? FuzzyDate.EARLIEST : this.scheduleDate.earliestPossible(); }
	public LocalDateTime latestPossible() { return this.isOutOfOrder() ? FuzzyDate.LATEST : this.scheduleDate.latestPossible(); }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getNote() { return this.note; }
	public void setNote(String note) { this.note = note; }
	
	public void setCompleted() { this.completedTimeStamp = TimeDateUtil.getCurrentDateTime(); }
	public boolean isCompleted() { return this.completedTimeStamp != null; }
	public LocalDateTime getCompletedTime() { return this.completedTimeStamp; }
	public EnumPublicness getTagPublicness() { return tagPublicness; }
	public void setTagPublicness(EnumPublicness tagPublicness) { this.tagPublicness = tagPublicness; }
	public EnumScheduleType getTagScheduleType() { return tagScheduleType; }
	public void setTagScheduleType(EnumScheduleType tagScheduleType) { this.tagScheduleType = tagScheduleType; }
	public EnumIsPlaceHolder getTagPlaceHolder() { return tagPlaceHolder; }
	public void setTagPlaceHolder(EnumIsPlaceHolder tagPlaceHolder) { this.tagPlaceHolder = tagPlaceHolder; }
	public EnumIsPending getTagPending() { return tagPending; }
	public void setTagPending(EnumIsPending tagPending) { this.tagPending = tagPending; }
	public EnumUrgent getTagUrgent() { return this.tagUrgent; }
	public void setTagUrgent(EnumUrgent tagUrgent) { this.tagUrgent = tagUrgent; }
	
	public boolean isOutOfOrder() { return this.outofOrder; }
	public void setOutOfOrder(boolean outoforder) { this.outofOrder = outoforder; }
	
	@Override
	public ScheduleEntry clone() {
		try {
			ScheduleEntry clone = (ScheduleEntry) super.clone();
			clone.scheduleDate = this.scheduleDate.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
//		if (obj instanceof ScheduleEntry) {
//			ScheduleEntry other = (ScheduleEntry) obj;
//			return other.getYear() == this.getYear() &&
//					other.getMonth().equals(this.getMonth()) &&
//					other.getDay() == this.getDay() &&
//					other.getTime() == this.getTime() &&
//					other.getTitle().equals(this.getTitle()) &&
//					other.getNote().equals(this.getNote()) &&
//					other.isCompleted() == this.isCompleted() &&
//					other.getTagPublicness().equals(this.getTagPublicness()) &&
//					other.getTagScheduleType().equals(this.getTagScheduleType()) &&
//					other.getTagPlaceHolder().equals(this.getTagPlaceHolder()) &&
//					other.getTagPending().equals(this.getTagPending()) &&
//					other.getTagUrgent().equals(this.getTagUrgent());
//			
//		} else
		{
			return super.equals(obj);
		}
	}
}
