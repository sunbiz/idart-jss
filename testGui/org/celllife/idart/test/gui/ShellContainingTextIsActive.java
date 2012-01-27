package org.celllife.idart.test.gui;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.utils.StringUtils;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * A condition that waits until a shell with title text
 * containing the specified text is active.
 *
 * @see Conditions
 */
class ShellContainingTextIsActive extends DefaultCondition {

	private String	text;

	ShellContainingTextIsActive(String text) {
		Assert.isNotNull(text, "The shell text was null"); //$NON-NLS-1$
		Assert.isLegal(!StringUtils.isEmpty(text), "The shell text was empty"); //$NON-NLS-1$
		this.text = text;
	}

	@Override
	public String getFailureMessage() {
		return "The shell '%" + text + "%' did not activate"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean test() throws Exception {
		try {
			final SWTBotShell[] shells = bot.shells();
			for (final SWTBotShell shell : shells) {
				if (shell.getText().contains(text)){
					 Boolean isActive = UIThreadRunnable.syncExec(new BoolResult() {
							@Override
							public Boolean run() {
								return shell.widget.isVisible() || shell.widget.isFocusControl();
							}
					});
					if (isActive){
						return true;
					}
				}
			}
		} catch (WidgetNotFoundException e) {
		}
		return false;
	}

}
