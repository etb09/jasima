package minifab.control;

import jasima.shopSim.core.PR;
import jasima.shopSim.core.PrioRuleTarget;
import jasima.shopSim.prioRules.basic.TieBreakerFASFS;
import minifab.model.MiniFabOperation;
import minifab.model.MiniFabWithOpsExperiment;
import minifab.model.OperatorGroup;

/**
 * This is a kind of dummy PR for machine resources that only looks at the
 * decisions of the operator pool and assigns the right priority values to match
 * the operator's decision.
 * 
 * @author Torsten Hildebrandt
 */
@SuppressWarnings("serial")
public class MachinesAsOpSlavesPR extends PR {

	private OperatorGroup ops;

	public MachinesAsOpSlavesPR() {
		super();

		setTieBreaker(new TieBreakerFASFS());
	}

	@Override
	public void init() {
		super.init();

		ops = (OperatorGroup) getOwner().shop().getWorkstationByName(
				MiniFabWithOpsExperiment.OPS_POOL_NAME);
		assert ops != null;
	}

	@Override
	public boolean keepIdle() {
		MiniFabOperation op = (MiniFabOperation) ops.jobLastStarted()
				.getCurrentOperation();
		return op.machine != getOwner();
	}

	@Override
	public double calcPrio(PrioRuleTarget entry) {
		if (entry == ops.jobLastStarted()) {
			return 1;
		} else {
			return 0;
		}
	}

}
