/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wwem;

import oms3.annotations.In;
import oms3.annotations.Initialize;
import oms3.control.If;

/**
 *
 * @author od
 */
public class WaterErosion extends If {

    Profil profil = new Profil();
    Hyd2ero hyd2ero = new Hyd2ero();
    StripRunoffZeros stripRunoffZeros = new StripRunoffZeros();
    Kinwave kinwave = new Kinwave();
    Xinflo xinflo = new Xinflo();
    Param param = new Param();
    Route route = new Route();
    Sloss sloss = new Sloss();
    Eprint eprint = new Eprint();
    Sedout sedout = new Sedout();
    
    //
    Daily loop;

    public WaterErosion(Daily loop) {
        this.loop = loop;
    }

    @Initialize
    public void init() throws Exception {
        conditional(loop, "qoutflag");
    }
}
