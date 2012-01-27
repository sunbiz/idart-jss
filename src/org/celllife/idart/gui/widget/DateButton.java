package org.celllife.idart.gui.widget;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.celllife.function.DateRule;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.misc.iDARTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.vafada.swtcalendar.SWTCalendarDialog;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

/**
 * DateButton is an extension of {@link Button} that displays a date as the text
 * on the button and pops up a calendar dialog when the button is pressed
 * allowing the user to select a date.
 * 
 * The class also supports restricting valid dates by adding a combination of
 * {@link DateRule} instances. Each <code>DateRule</code> must return true when
 * evaluated for the selected date in order for the date to be valid.
 * 
 * Example Usage:
 * 
 * <pre>
 * DateButton b = new DateButton(shell, SWT.PUSH);
 * b.setDate(initialDate);
 * </pre>
 */
public class DateButton extends Button {

	public static int NONE = 0;
	public static int ZERO_TIMESTAMP = 1;
	private static final String DATE = "        Date        ";
	private final Set<DateChangedListener> listeners;
	private String errorMessage;
	private String errorMessageTitle = "Date Error";
	private IObjectInputValidator validator;
	private final int style;
	private boolean snap = true;

	/**
	 * @param parent
	 * @param style
	 *            DateButton.NONE or DateButton.ZERO_TIMESTAMP
	 * @param validator
	 */
	public DateButton(Composite parent, int style,
			IObjectInputValidator validator) {
		super(parent, SWT.PUSH);
		this.style = style;
		this.validator = validator;
		listeners = new HashSet<DateChangedListener>();
		this.setText(DATE);
		this.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
		final DateButton thisButton = this;

		this.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				super.widgetSelected(event);
				final SWTCalendarDialog cal = new SWTCalendarDialog(getShell(),
						snap ? DateButton.this : null);
				if (getDate() != null) {
					cal.setDate(getDate());
				} else {
					cal.setDate(new Date());
				}

				cal.addDateChangedListener(new SWTCalendarListener() {
					@Override
					public void dateChanged(SWTCalendarEvent calendarEvent) {
						if (calendarEvent.type == SWTCalendarEvent.DAY) {
							cal.close();
							try {
								Date selectedDate = calendarEvent.getCalendar()
								.getTime();
								thisButton.setDate(selectedDate);
							} catch (RuntimeException e) {
								displayFailureMessage();
							}
						}
					}
				});
				cal.open();
			}

		});
	}

	/**
	 * If set to true the calendar dialog will appear below the button (if the control
	 * is not null) otherwise it will be centered.
	 * 
	 * Default = true
	 * 
	 * @param snap
	 */
	public void snapToControl(boolean snap){
		this.snap = snap;
	}

	/**
	 * Clear Get the current date represented by the button.
	 * 
	 */
	public void clearDate() {
		this.setText(DATE);
		super.setData(null);
	}

	/**
	 * Get the current date represented by the button.
	 * 
	 * @return the Date if it has been set or null.
	 */
	public Date getDate() {
		Object data = super.getData();
		if (data != null && data instanceof Date)
			if (style == ZERO_TIMESTAMP)
				return iDARTUtil.zeroTimeStamp((Date) data);
			else
				return (Date) data;
		else
			return null;
	}

	/**
	 * Use setDate() instead.
	 */
	@Override
	@Deprecated
	public void setData(Object o) {
		if (o instanceof Date) {
			Date date = (Date) o;
			setDate(date);
		}
	}

	/**
	 * Use getDate() instead.
	 */
	@Override
	@Deprecated
	public Object getData() {
		return getDate();
	}

	/**
	 * Set the date for the button.
	 * 
	 * @param date
	 * @throws DateException
	 *             if the date is not valid according to the restrictions.
	 */
	public void setDate(Date date) throws RuntimeException {
		Date previousDate = getDate();
		if (date == null) {
			clearDate();
		} else if (validate(date)) {
			this.setText(iDARTUtil.format(date));
			super.setData(date);
			fireDateChangedEvent(date, previousDate);
		} else
			throw new RuntimeException(getValidationMessage());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Validates the input.
	 * <p>
	 * The default implementation of this method delegates the request to the
	 * supplied input validator object; if it finds the input invalid, the error
	 * message is displayed in a message box. This hook method is called
	 * whenever the date changes.
	 * </p>
	 * 
	 * @param newDate
	 *            the date to be validated
	 * @return true if the date is valid
	 */
	private boolean validate(Date selectedDate) {
		errorMessage = null;
		if (validator != null) {
			errorMessage = validator.isValid(selectedDate);
		}
		return errorMessage == null;
	}

	private void displayFailureMessage() {
		MessageBox mb = new MessageBox(getShell(), SWT.OK | SWT.ICON_ERROR);
		mb.setText(getErrorMessageTitle());
		mb.setMessage(errorMessage);
		mb.open();
	}

	private String getValidationMessage() {
		return errorMessage;
	}

	public void setValidator(IObjectInputValidator validator) {
		this.validator = validator;
	}

	public void addDateChangedListener(DateChangedListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeDateChangedListener(DateChangedListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	private void fireDateChangedEvent(Date newDate, Date previousDate) {
		DateChangedEvent event = new DateChangedEvent(newDate, previousDate);
		for (DateChangedListener listener : listeners) {
			listener.dateChanged(event);
		}
	}
	/**
	 * Sets the value of the error message that will be displayed in the message box
	 * @param errorMessageTitle
	 */
	public void setErrorMessageTitle(String errorMessageTitle) {
		this.errorMessageTitle = errorMessageTitle;
	}

	public String getErrorMessageTitle() {
		return errorMessageTitle;
	}
}
