package pz.ajneb97.versions.classes;


import pz.ajneb97.utils.ServerVersion;

public abstract class NMSClass {
    protected Class<?> classReference;

    public void setClassReference(Class<?> classReference) {
        this.classReference = classReference;
    }

    public Class<?> getClassReference() {
        return classReference;
    }

    protected boolean serverVersionGreaterEqualThan(ServerVersion serverVersion, ServerVersion versionToCheck){
        return serverVersion.ordinal() >= versionToCheck.ordinal();
    }
}
