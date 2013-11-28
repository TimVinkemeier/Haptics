package de.timvinkemeier.haptics.core;

import javax.activity.InvalidActivityException;

import de.timvinkemeier.haptics.configuration.ExecutionState;
import de.timvinkemeier.haptics.configuration.Schedule;
import de.timvinkemeier.haptics.configuration.ScheduleItem;
import de.timvinkemeier.haptics.exceptions.MissingDefinitionException;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class HAction.
 */
public class HAction extends HJobBase {

	/**
	 * Run.
	 *
	 * @see de.timvinkemeier.haptics.core.HJobBase#run()
	 */
	@Override
	public void run() {
		try {
			executionState = ExecutionState.Running;
			Log.print("'" + getName() + "' started.", LogLevel.Verbose);
			preScript.run();
			postScript.run();
			executionState = ExecutionState.Finished;
			Log.print("'" + getName() + "' finished.", LogLevel.Verbose);
		} catch (InterruptedException ex) {
			executionState = ExecutionState.Killed;
			Log.print("Item '" + si.getID() + "' killed.", LogLevel.Info);
		} catch (Exception ex) {
			executionState = ExecutionState.Exception;
			Log.print("The following error occurred during execution of action '" + getName() + "'. Further execution will be aborted...", LogLevel.Critical);
			Log.print(ex);
			Schedule.asyncException = ex;
		}
	}

	/**
	 * Instantiates a new HAction.
	 * 
	 * @param si
	 *            the si
	 * @throws MissingDefinitionException
	 *             the missing definition exception
	 * @throws InvalidActivityException
	 *             the invalid activity exception
	 */
	public HAction(ScheduleItem si) throws MissingDefinitionException, InvalidActivityException {
		this.si = si;
		if (!si.isAction()) {
			throw new InvalidActivityException("Cannot create HAction from non-action item '" + si.getID() + "'.");
		}
		if (si.getPreScriptLocation() == null || si.getPreScript() == null || si.getPostScriptLocation() == null || si.getPostScript() == null) {
			throw new MissingDefinitionException("No pre- or postScript given for action '" + getName() + "'.");
		}
	}

	/**
	 * From schedule item.
	 * 
	 * @param si
	 *            the si
	 * @return the h job base
	 * @throws InvalidActivityException
	 *             the invalid activity exception
	 * @throws MissingDefinitionException
	 *             the missing definition exception
	 */
	public static HJobBase fromScheduleItem(ScheduleItem si) throws InvalidActivityException, MissingDefinitionException {
		HAction action = new HAction(si);
		Log.print("Action for item '" + si.getID() + "' created.", LogLevel.Verbose);
		return action;
	}
}
