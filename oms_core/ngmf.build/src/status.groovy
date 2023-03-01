/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//
String log = new File("/od/projects/svnlog.txt").getText();

oms_fix = [];
oms_new = [];

log.split(/\-{72}/).each { String v -> 
    myMatcher = (v.replace("\n", " ").trim() =~ /r([0-9]+) \| (.*?) \| (.*?) \| ([12]) lines?  (.*)?/);
	if (myMatcher.matches()) {
        int rev = myMatcher[0][1].toInteger();
        String who = myMatcher[0][2];
        String when = myMatcher[0][3];
        String what = myMatcher[0][5];

		println rev+": "+ who +"  "+ when +"  "+myMatcher[0][4] +" '"+ what + "'"
        if (what.startsWith("Branding")) {
            oms_fix << what
        } else if (what.startsWith("added")) {
            oms_new << what
        }
    }
}

println "-------------------"
println oms_fix
//println oms_new


String info = "!Features\n"
oms_fix.each{
    info = info + " * $it \n"
}

println info
 