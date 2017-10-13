/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes.KismetInputLink;
import cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes.KismetLinkTarget;
import cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes.KismetOutputLink;
import cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes.KismetVariableLink;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract predecessor for all objects that are part of Kismet.
 * Declares utitlity methods for handling input, output and variable links.
 * The object must be initialized with link names in correct order (internally, the links
 * are referenced by number solely).
 * @author Martin Cerny
 */
public abstract class AbstractKismetObject extends AbstractObject {

    private transient Map<String, Integer> inputLinksMapping = new HashMap<String, Integer>();
    private transient Map<String, Integer> outputLinksMapping = new HashMap<String, Integer>();
    private transient Map<String, Integer> variableLinksMapping = new HashMap<String, Integer>();
    
    private List<KismetInputLink> inputLinks;
    private List<KismetOutputLink> outputLinks;
    private List<KismetVariableLink> variableLinks;

    private Sequence parentSequence;
    
    @FieldName("ObjInstanceVersion")
    private Integer instanceVersion = 1;

    private String objComment;
    private Boolean outputObjCommentToScreen;
    private Boolean suppressAutoComment;
    
    /**
     * The location of the object for GUI
     */
    private Integer objPosX;
    private Integer objPosY;
    
    public AbstractKismetObject(String className, String archetypeName, String[] inputLinkNames, String[] outputLinkNames, String[] variableLinkNames) {
        super(className,archetypeName);
        init(inputLinkNames, outputLinkNames, variableLinkNames);
    }

    public AbstractKismetObject(String className, UnrealReference archetype, String[] inputLinkNames, String[] outputLinkNames, String[] variableLinkNames) {
        super(className, archetype);
        init(inputLinkNames, outputLinkNames, variableLinkNames);
    }

    private void init(String[] inputLinkNames, String[] outputLinkNames,String[] variableLinkNames) {

        inputLinks = new ArrayList<KismetInputLink>(inputLinkNames.length);
        for(int i = 0; i < inputLinkNames.length; i++){
            inputLinks.add(new KismetInputLink(null));
            addInputLinkMapping(inputLinkNames[i], i);
        }
        outputLinks = new ArrayList<KismetOutputLink>(outputLinkNames.length);
        for(int i = 0; i < outputLinkNames.length; i++){
            outputLinks.add(new KismetOutputLink(new ArrayList<KismetLinkTarget>()));
            addOutputLinkMapping(outputLinkNames[i], i);
        }
        variableLinks = new ArrayList<KismetVariableLink>(variableLinkNames.length);
        for(int i = 0; i < variableLinkNames.length; i++){
            variableLinks.add(new KismetVariableLink(new ArrayList<UnrealReference>()));
            addVariableLinkMapping(variableLinkNames[i], i);
        }
    }

    private void addInputLinkMapping(String linkName, int linkIndex){
        if(linkIndex >= inputLinks.size()){
            throw new IndexOutOfBoundsException("Link " + linkIndex + " not found");
        }
        inputLinksMapping.put(linkName, linkIndex);
    }

    private void addOutputLinkMapping(String linkName, int linkIndex){
        if(linkIndex >= outputLinks.size()){
            throw new IndexOutOfBoundsException("Link " + linkIndex + " not found");
        }
        outputLinksMapping.put(linkName, linkIndex);
    }
    
    private void addVariableLinkMapping(String linkName, int linkIndex){
        if(linkIndex >= variableLinks.size()){
            throw new IndexOutOfBoundsException("Link " + linkIndex + " not found");
        }
        variableLinksMapping.put(linkName, linkIndex);
    }

    /**
     * Adds a variable link with first avaliable index and given name.
     * @param name
     */
    protected void addVariableLink(String name){
        int linkIndex = variableLinks.size();
        variableLinks.add(new KismetVariableLink(new ArrayList<UnrealReference>()));
        addVariableLinkMapping(name, linkIndex);
    }

    public void setInputLinkMappingByNamesList(String[] linkNames){
        for(int i = 0; i < linkNames.length; i++){
            addInputLinkMapping(linkNames[i], i);
        }
    }

    public void setOutputLinkMappingByNamesList(String[] linkNames){
        for(int i = 0; i < linkNames.length; i++){
            addOutputLinkMapping(linkNames[i], i);
        }
    }

    public void setVariableLinkMappingByNamesList(String[] linkNames){
        for(int i = 0; i < linkNames.length; i++){
            addVariableLinkMapping(linkNames[i], i);
        }
    }

    public void setInputLink(String linkName, KismetInputLink link){
        if(link == null){
            throw new NullPointerException("Link cannot be set to null");
        }
        Integer linkIndex = inputLinksMapping.get(linkName);
        if(linkIndex == null){
            throw new IllegalArgumentException("Mapping for input link '" + linkName + "' not found.");
        }
        inputLinks.set(linkIndex,link);
    }

    public int getInputLinkIndex(String linkName){
        return inputLinksMapping.get(linkName);
    }

    public KismetInputLink getInputLink(String linkName){
        Integer linkIndex = inputLinksMapping.get(linkName);
        if(linkIndex == null){
            throw new IllegalArgumentException("Mapping for input link '" + linkName + "' not found.");
        }
        return inputLinks.get(linkIndex);
    }
    
    public void setIntputLinkTarget(String linkName, UnrealReference target){
        getInputLink(linkName).setTarget(target);
    }

    public void setOutputLink(String linkName, KismetOutputLink link){
        if(link == null){
            throw new NullPointerException("Link cannot be set to null");
        }
        Integer linkIndex = outputLinksMapping.get(linkName);
        if(linkIndex == null){
            throw new IllegalArgumentException("Mapping for output link '" + linkName + "' not found.");
        }
        outputLinks.set(linkIndex,link);
    }

    public KismetOutputLink getOutputLink(String linkName){
        Integer linkIndex = outputLinksMapping.get(linkName);
        if(linkIndex == null){
            throw new IllegalArgumentException("Mapping for output link '" + linkName + "' not found.");
        }
        return outputLinks.get(linkIndex);
    }

    /**
     * Adds a target to specified output link
     * @param linkName
     * @param target 
     */
    public void addOutputLinkTarget(String linkName,KismetLinkTarget target){
        getOutputLink(linkName).addLink(target);
    }

    /**
     * Sets a variable link, clearing all previous state.
     * @param linkName
     * @param link 
     */
    public void setVariableLink(String linkName, KismetVariableLink link){
        if(link == null){
            throw new NullPointerException("Link cannot be set to null");
        }
        Integer linkIndex = variableLinksMapping.get(linkName);
        if(linkIndex == null){
            throw new IllegalArgumentException("Mapping for variable link '" + linkName + "' not found.");
        }
        variableLinks.set(linkIndex,link);
    }

    public void addVariableLinkTarget(String linkName, UnrealReference target){
        getVariableLink(linkName).addLinkedVariable(target);
    }

    public KismetVariableLink getVariableLink(String linkName){
        Integer linkIndex = variableLinksMapping.get(linkName);
        if(linkIndex == null){
            throw new IllegalArgumentException("Mapping for variable link '" + linkName + "' not found.");
        }
        return variableLinks.get(linkIndex);
    }

    public Integer getInstanceVersion() {
        return instanceVersion;
    }

    public void setInstanceVersion(Integer instanceVersion) {
        this.instanceVersion = instanceVersion;
    }

    public String getObjComment() {
        return objComment;
    }

    public void setObjComment(String objComment) {
        this.objComment = objComment;
    }

    public Integer getObjPosX() {
        return objPosX;
    }

    public void setObjPosX(Integer objPosX) {
        this.objPosX = objPosX;
    }

    public Integer getObjPosY() {
        return objPosY;
    }

    public void setObjPosY(Integer objPosY) {
        this.objPosY = objPosY;
    }

    public Boolean getOutputObjCommentToScreen() {
        return outputObjCommentToScreen;
    }

    public void setOutputObjCommentToScreen(Boolean outputObjCommentToScreen) {
        this.outputObjCommentToScreen = outputObjCommentToScreen;
    }

    public Sequence getParentSequence() {
        return parentSequence;
    }

    public void setParentSequence(Sequence parentSequence) {
        this.parentSequence = parentSequence;
    }

    public Boolean getSuppressAutoComment() {
        return suppressAutoComment;
    }

    public void setSuppressAutoComment(Boolean suppressAutoComment) {
        this.suppressAutoComment = suppressAutoComment;
    }

    public List<KismetInputLink> getInputLinks() {
        return inputLinks;
    }

    public List<KismetOutputLink> getOutputLinks() {
        return outputLinks;
    }

    public List<KismetVariableLink> getVariableLinks() {
        return variableLinks;
    }


    
    
    public void setPositionForGUI(int x, int y){
        setObjPosX(x);
        setObjPosY(y);
    }
    
}
