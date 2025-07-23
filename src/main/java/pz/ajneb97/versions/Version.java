package pz.ajneb97.versions;


import pz.ajneb97.versions.classes.NMSClass;

import java.util.HashMap;

public class Version {
    private HashMap<NMSClassType, NMSClass> classes;

    public Version(){
        this.classes = new HashMap<>();
    }

    public void addClass(NMSClassType name, NMSClass nmsClass){
        classes.put(name,nmsClass);
    }

    public NMSClass getClass(NMSClassType name){
        return classes.get(name);
    }
}
