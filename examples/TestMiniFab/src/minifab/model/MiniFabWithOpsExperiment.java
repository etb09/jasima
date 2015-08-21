package minifab.model;

import jasima.core.random.continuous.DblConst;
import jasima.core.random.continuous.DblStream;
import jasima.core.simulation.arrivalprocess.ArrivalsStationary;
import jasima.core.util.TypeUtil;
import jasima.shopSim.core.DynamicJobSource;
import jasima.shopSim.core.JobShopExperiment;
import jasima.shopSim.core.PR;
import jasima.shopSim.core.Route;
import jasima.shopSim.core.WorkStation;
import minifab.control.CheckResourceAvailability;
import minifab.control.MachinesAsOpSlavesPR;

@SuppressWarnings("serial")
public class MiniFabWithOpsExperiment extends JobShopExperiment {

	public static final int[] LOAD_TIMES = { 10, 0, 5, 0, 0, 0 };
	public static final int[] PROC_TIMES = { 20, 10, 10, 80, 105, 30 };
	public static final int[] UNLOAD_TIMES = { 10, 15, 10, 0, 0, 0 };
	public static final String[] WORKSTATIONS = { "G1_Mab", "G2_Mcd", "G3_Me",
			"G2_Mcd", "G1_Mab", "G3_Me" };
	public static final String OPS_POOL_NAME = "Ops";

	private int numOperators = 2;
	private DblStream interArrivalTimes = new DblConst(116.63);

	protected OperatorGroup ops;

	public MiniFabWithOpsExperiment() {
		super();
	}

	@Override
	protected void createShop() {
		super.createShop();

		createResources();
		createRoute();
		createJobSource();
	}

	private void createResources() {
		SyncMachinesAndOps sync = new SyncMachinesAndOps();

		WorkStation ma = new WorkStation(2);
		ma.setName("G1_Mab");
		ma.addNotifierListener(sync);
		shop.addMachine(ma);

		WorkStation mb = new WorkStation(2);
		mb.setName("G2_Mcd");
		mb.addNotifierListener(sync);
		shop.addMachine(mb);

		WorkStation mc = new WorkStation(1);
		mc.setName("G3_Me");
		mc.addNotifierListener(sync);
		shop.addMachine(mc);

		ops = new OperatorGroup(getNumOperators());
		ops.setName(OPS_POOL_NAME);
		shop.addMachine(ops);
	}

	private void createRoute() {
		assert ops != null || getNumOperators() == 0;

		Route r = new Route();

		// check length of parameter arrays
		assert LOAD_TIMES.length == PROC_TIMES.length;
		assert PROC_TIMES.length == UNLOAD_TIMES.length;
		assert UNLOAD_TIMES.length == WORKSTATIONS.length;

		for (int i = 0; i < LOAD_TIMES.length; i++) {
			WorkStation ws = shop.getWorkstationByName(WORKSTATIONS[i]);

			MiniFabOperation load = new MiniFabOperation();
			load.machine = ws;
			load.operator = ops;
			load.procTime = LOAD_TIMES[i];

			MiniFabOperation proc = new MiniFabOperation();
			proc.machine = ws;
			proc.operator = null;
			proc.procTime = PROC_TIMES[i];

			MiniFabOperation unload = new MiniFabOperation();
			unload.machine = ws;
			unload.operator = ops;
			unload.procTime = UNLOAD_TIMES[i];

			load.loadOp = load;
			load.procOp = proc;
			load.unloadOp = unload;

			proc.loadOp = load;
			proc.procOp = proc;
			proc.unloadOp = unload;

			unload.loadOp = load;
			unload.procOp = proc;
			unload.unloadOp = unload;

			r.addSequentialOperation(load);
			r.addSequentialOperation(proc);
			r.addSequentialOperation(unload);
		}

		shop.addRoute(r);
	}

	private void createJobSource() {
		DynamicJobSource js = new DynamicJobSource();

		assert shop.routes.length == 1;
		js.setRoute(shop.routes[0]);

		ArrivalsStationary arrivals = new ArrivalsStationary(
				TypeUtil.cloneIfPossible(getInterArrivalTimes()));
		js.setArrivalProcess(arrivals);

		shop.addJobSource(js);
	}

	@Override
	protected void postConfigShop() {
		super.postConfigShop();

		// overwrite sequencingRules, only opGroup get "sequencingRule"
		for (WorkStation ws : shop.machines) {
			if (ws instanceof OperatorGroup) {
				PR currentPR = ws.queue.getSequencingRule();
				ws.queue.setSequencingRule(new CheckResourceAvailability()
						.setFinalTieBreaker(currentPR));
			} else {
				MachinesAsOpSlavesPR pr = new MachinesAsOpSlavesPR();
				pr.setOwner(ws);
				ws.queue.setSequencingRule(pr);
			}
		}
	}

	public int getNumOperators() {
		return numOperators;
	}

	public void setNumOperators(int numOps) {
		if (numOps < 1)
			throw new IllegalArgumentException();

		this.numOperators = numOps;
	}

	public DblStream getInterArrivalTimes() {
		return interArrivalTimes;
	}

	public void setInterArrivalTimes(DblStream interArrivalTimes) {
		this.interArrivalTimes = interArrivalTimes;
	}

}
