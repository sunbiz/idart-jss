package org.celllife.idart.misc.task;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.celllife.idart.misc.MessageUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Utility class for running tasks.
 */
public class TaskManager {

	private static Map<String, Class<? extends IdartTask>> taskMap = new HashMap<String, Class<? extends IdartTask>>();

	static {
		// add all available tasks to to map
		taskMap.put("import", Import.class);
		taskMap.put("doIndexCheck", PackagedDrugsIndexCheck.class);
		taskMap.put("merge", MergeDbs.class);
		taskMap.put("recalculateStock", RecalculateSockTask.class);
	}

	private TaskManager() {
	}

	public static void runTask(String taskName, String[] args) {
		final IdartTask task = getTask(taskName);

		if (task != null) {
			try {
				if (args.length != 0 && (args[0].toUpperCase().contains("help") ||
						args[0].equalsIgnoreCase("-h") ||
						args[0].contains("?"))){
					printHelp(task);
					return;
				}
				
				boolean initSuccessful = task.init(args);
				if (!initSuccessful) {
					printHelp(task);
					return;
				}

				new ProgressMonitorDialog(null).run(true, true, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						try {
							task.run(monitor);
						} catch (TaskException e) {
							throw new InvocationTargetException(e);
						}
					}
				});

				MessageDialog
				.openInformation(
						null,
						"Task completed successfully",
						"The "
						+ taskName
						+ " has been completed successfully. Check the error file for any errors.");
			} catch (InvocationTargetException e) {
				MessageUtil.showError(e, "Error running task", taskName
						+ " encountered an error.");
			} catch (InterruptedException e) {
				MessageDialog
				.openInformation(null, "Cancelled", e.getMessage());
			}
		} else {
			System.out.println("Available tasks:");
			for (String key : taskMap.keySet()) {
				System.out.println(key + " : " + getTask(key).getDescription());
			}
		}
	}

	/**
	 * @param task
	 */
	private static void printHelp(final IdartTask task) {
		String help = task.getDescription() + "\nHelp documentation\n" +
				"=============================\n" + task.getHelpText();
		MessageDialog.openError(null, "Task help",help );
		System.out.println(help);
	}

	private static IdartTask getTask(String taskName) {
		Class<? extends IdartTask> taskClass = taskMap.get(taskName);
		if (taskClass == null) {
			String message = "Task not found";
			MessageDialog.openError(null, message, "Unknown task: '"
					+ taskName + "'\n Available options are:\n."
					+ printTaskNames());
			System.out.println(message);
			return null;
		}

		IdartTask task = null;
		try {
			task = taskClass.newInstance();
		} catch (InstantiationException e) {
			MessageUtil.showError(e, "Error running task",
					"Unable to run task: " + taskName);
		} catch (IllegalAccessException e) {
			MessageUtil.showError(e, "Error running task",
					"Unable to run task: " + taskName);
		}
		return task;
	}

	private static String printTaskNames() {
		String names = "";
		for (String name : taskMap.keySet()) {
			IdartTask task = getTask(name);
			String description = task == null ? "" : task.getDescription();
			names += name + "	: " + description + "\n";
		}
		return names;
	}
}
