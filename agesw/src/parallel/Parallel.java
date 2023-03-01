/*
 * This file is part of the AgroEcoSystem-Watershed (AgES-W) model component
 * collection. AgES-W components are derived from multiple agroecosystem models
 * including J2K and J2K-SN (FSU-Jena, DGHM, Germany), SWAT (USDA-ARS, USA),
 * WEPP (USDA-ARS, USA), RZWQM2 (USDA-ARS, USA), and others.
 *
 * The AgES-W model is free software; you can redistribute the model and/or
 * modify the components under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package parallel;

import ages.types.HRU;
import ages.types.StreamReach;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import oms3.annotations.*;
import oms3.util.Threads;
import oms3.util.Threads.CompList;
import static oms3.util.Threads.seq_e;
import static oms3.util.Threads.shutdownAndAwaitTermination;

@Description("Add Parallel module definition here")
@Author(name = "Daniel Elliott, Nathan Lighthart", contact = "jim.ascough@ars.usda.gov")
@Keywords("Insert keywords")
@Bibliography("Insert bibliography here")
@VersionInfo("$ID:$")
@SourceInfo("http://javaforge.com/scm/file/3979484/default/src/parallel/Parallel.java")
@License("http://www.gnu.org/licenses/gpl.html")
@Status(Status.TESTED)
@Documentation("src/parallel/Parallel.xml")
public class Parallel {
    private static ThreadPoolExecutor tPool = null; // used for disjoint forest method

    private static ThreadPoolExecutor getES() {
        if (tPool == null) {
            int nthreads = Runtime.getRuntime().availableProcessors() + 1;
            tPool = new ThreadPoolExecutor(nthreads, nthreads, 20L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            tPool.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdownAndAwaitTermination(tPool);
                }
            });

        }
        return tPool;
    }

    /*
	 * orderHRUs contains a list of HRU objects. Each HRU must have a correct value for
	 * HRU.depth. parallelType may be null, "", or "seq" for linear topological
	 * sort, "tango" for tango sort, "forest" for disjoint forest sort, or "layer"
         * for layered topological sort. Returns an ordering of HRUs for process creation.
     */
    public static List<List<HRU>> orderHRUs(List<HRU> hrus, String parallelType) {
        List<List<HRU>> hruList = null;

        if (parallelType == null || parallelType.equals("")) {
            parallelType = "seq";
        }

        if (parallelType.equalsIgnoreCase("seq")) {
            hruList = new ArrayList<List<HRU>>();
            hruList.add(hrus);
        } else if (parallelType.equalsIgnoreCase("tango")) {
            hruList = TangoSort.sortHRUs(hrus);
        } else if (parallelType.equalsIgnoreCase("forest")) {
            hruList = DisjointForest.getForestHRUs(hrus);
        } else if (parallelType.equalsIgnoreCase("layer")) {
            hruList = Layer.layerSortHRUs(hrus);
        } else { // if value is not recognized, use sequential ordering
            hruList = orderHRUs(hrus, "seq");
        }
        return hruList;
    }

    /*
	 * orderReaches contains a list of StreamReach objects. Each StreamReach must have a
	 * correct value for StreamReach.depth. parallelType may be null, "", or
         * "seq" for linear topological sort, "tango" for tango sort, "forest" for
         * disjoint forest sort, or "layer" for layered topological sort. Returns
         * an ordering of stream reaches for process creation.
     */
    public static List<List<StreamReach>> orderReaches(List<StreamReach> reaches, String parallelType) {
        List<List<StreamReach>> reachList = null;

        if (parallelType == null || parallelType.equals("")) {
            parallelType = "seq";
        }

        if (parallelType.equalsIgnoreCase("seq")) {
            reachList = new ArrayList<>();
            reachList.add(reaches);
        } else if (parallelType.equalsIgnoreCase("tango")) {
            reachList = TangoSort.sortReaches(reaches);
        } else if (parallelType.equalsIgnoreCase("forest")) {
            reachList = DisjointForest.getForestReaches(reaches);
        } else if (parallelType.equalsIgnoreCase("layer")) {
            reachList = Layer.layerSortReaches(reaches);
        } else { // if value is not recognized, use sequential ordering
            reachList = orderReaches(reaches, "seq");
        }
        return reachList;
    }

    /*
	 * executeHRUs contains a list of HRU objects. Each HRU must have a correct value for
	 * HRU.depth. parallelType may be null, "", or "seq" for linear topological
	 * sort, "tango" for tango sort, "forest" for disjoint forest sort, or "layer"
         * for layered topological sort. Returns an ordering of HRUs for process creation.
     */
    public static void executeHRUs(List<CompList<HRU>> list, String parallelType) throws Exception {
        if (parallelType == null || parallelType.equals("")) {
            parallelType = "seq";
        }

        if (parallelType.equalsIgnoreCase("seq")) {
            for (CompList<HRU> l : list) {
                Threads.seq_e(l);
            }
        } else if (parallelType.equalsIgnoreCase("tango") || parallelType.equalsIgnoreCase("layer")) {
            for (CompList<HRU> l : list) {
                Threads.par_e(l);
            }
        } else if (parallelType.equalsIgnoreCase("forest")) {

            tPool = getES();
            final CountDownLatch latch = new CountDownLatch(list.size());
            for (final CompList<HRU> l : list) {
                tPool.submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            seq_e(l);
                        } catch (Throwable E) {
                            shutdownAndAwaitTermination(tPool);
                        }
                        latch.countDown();
                    }
                });
            }
            latch.await();
        } else { // if value is not recognized, use sequential ordering
            executeHRUs(list, "seq");
        }
    }

    /*
	 * executeReaches contains a list of StreamReach objects. Each StreamReach must have a
	 * correct value for StreamReach.depth. parallelType may be null, "", or
         * "seq" for linear topological sort, "tango" for tango sort, "forest" for
         * disjoint forest sort, or "layer" for layered topological sort. Returns
         * an ordering of stream reaches for process creation.
     */
    public static void executeReaches(List<CompList<StreamReach>> list, String parallelType) throws Exception {
        if (parallelType == null || parallelType.equals("")) {
            parallelType = "seq";
        }

        if (parallelType.equalsIgnoreCase("seq")) {
            for (CompList<StreamReach> l : list) {
                Threads.seq_e(l);
            }
        } else if (parallelType.equalsIgnoreCase("tango") || parallelType.equalsIgnoreCase("layer")) {
            for (CompList<StreamReach> l : list) {
                Threads.par_e(l);
            }
        } else if (parallelType.equalsIgnoreCase("forest")) {

            tPool = getES();
            final CountDownLatch latch = new CountDownLatch(list.size());
            for (final CompList<StreamReach> l : list) {
                tPool.submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            seq_e(l);
                        } catch (Throwable E) {
                            shutdownAndAwaitTermination(tPool);
                        }
                        latch.countDown();
                    }
                });
            }
            latch.await();
        } else { // if value is not recognized, use sequential ordering
            executeReaches(list, "seq");
        }
    }
}
