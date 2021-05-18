package qwertzite.schedulemanager.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class WidgetUtil {
	
	/**
	 * Text style must be MULTI
	 * @param text
	 * @return
	 */
	public static Text makeTextAutoScroll(Text text) {
		Listener scrollBarListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				Text t = (Text) event.widget;
				Rectangle r1 = t.getClientArea(); // a rectangle which describes the area of the receiver
				Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height); // bounding rectangle which would be required to produce that client area. 
//				boolean hBar = (t.getStyle() & SWT.H_SCROLL) != 0;
//				boolean vBar = (t.getStyle() & SWT.V_SCROLL) != 0;
//				Point p = t.computeSize(!hBar ? r1.x : SWT.DEFAULT, !vBar ? r1.y : SWT.DEFAULT, true);
				Point p = t.computeSize(SWT.DEFAULT, r1.height, true);
				t.getHorizontalBar().setVisible(r1.width <= p.x);
				r1 = t.getClientArea();
				p = t.computeSize(r1.width, SWT.DEFAULT, true);
				t.getVerticalBar().setVisible(r1.height <= p.y);
				
//				Point p = t.computeSize(r1.x, r1.y, true);
				System.out.println("r1=" + r1 + ",r2=" + r2 + ",p="+p + ",sh=" + (r1.width <= p.x) + ",sv=" + (r1.height <= p.y));
//				if (hBar) 
//				if (vBar)
				if (event.type == SWT.Modify) {
					t.getParent().layout(true);
					t.showSelection();
				}
			}
		};
		text.addListener(SWT.Resize, scrollBarListener);
		text.addListener(SWT.Modify, scrollBarListener);
		
		return text;
	}
	
}
