package qwertzite.schedulemanager.client;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import qwertzite.schedulemanager.schedule.ScheduleEntry;
import qwertzite.schedulemanager.tag.EnumIsPending;
import qwertzite.schedulemanager.tag.EnumIsPlaceHolder;
import qwertzite.schedulemanager.tag.EnumPublicness;
import qwertzite.schedulemanager.tag.EnumScheduleType;
import qwertzite.schedulemanager.tag.EnumUrgent;

public class ScheduleFilter extends ViewerFilter {
	
	private boolean usePublicness = false;
	private EnumPublicness visiblePublicness = EnumPublicness.PUBLIC;
	private boolean showTaskOnly = false;
	private boolean hidePlaceholder = false;
	private boolean usePending = false;
	private EnumIsPending visiblePending = EnumIsPending.FALSE;
	private EnumUrgent urgency = EnumUrgent.NON_URGENT;
	
	public boolean isUsePublicness() { return usePublicness; }
	public void setUsePublicness(boolean usePublicness) { this.usePublicness = usePublicness; }
	public EnumPublicness getVisiblePublicness() { return visiblePublicness; }
	public void setVisiblePublicness(EnumPublicness visiblePublicness) { this.visiblePublicness = visiblePublicness; }
	public boolean isShowTaskOnly() { return showTaskOnly; }
	public void setShowTaskOnly(boolean showTaskOnly) { this.showTaskOnly = showTaskOnly; }
	public boolean isHidePlaceholder() { return hidePlaceholder; }
	public void setHidePlaceholder(boolean hidePlaceholder) { this.hidePlaceholder = hidePlaceholder; }
	public boolean isUsePending() { return usePending; }
	public void setUsePending(boolean usePending) { this.usePending = usePending; }
	public EnumIsPending getVisiblePending() { return visiblePending; }
	public void setVisiblePending(EnumIsPending visiblePending) { this.visiblePending = visiblePending; }
	public EnumUrgent getUrgency() { return urgency; }
	public void setUrgency(EnumUrgent urgency) { this.urgency = urgency; }
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ScheduleEntry entry = (ScheduleEntry) element;
		return (this.isUsePublicness() ? entry.getTagPublicness() == this.getVisiblePublicness() : true) &&
				(this.isShowTaskOnly() ? entry.getTagScheduleType() == EnumScheduleType.TASK : true) &&
				(this.isHidePlaceholder() ? entry.getTagPlaceHolder() != EnumIsPlaceHolder.TRUE : true) &&
				(this.isUsePending() ? entry.getTagPending() == this.getVisiblePending() : true) &&
				(entry.getTagUrgent().getIndex() <= this.getUrgency().getIndex());
	}
	
	@Override
	public String toString() {
		return "publicness=" + this.isUsePublicness() + "[" + this.getVisiblePublicness() +
				"],taskonly=" + this.isShowTaskOnly()
				+ ",hideplaceholder=" + this.isHidePlaceholder()
				+ ",peding=" + this.isUsePending() + "[" + this.getVisiblePending() + "]" + 
				"urgent=" + this.getUrgency();
	}
	
}
