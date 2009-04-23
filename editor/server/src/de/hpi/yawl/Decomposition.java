package de.hpi.yawl;

import java.util.*;

import de.hpi.bpmn.Edge;

public class Decomposition {
	
	private HashMap<String, Node> nodes = new HashMap(); // Maps node names to nodes
	private List<Edge> edges;
	
    private String id; // The id of the decomposition
    private boolean isRootNet; // Whether this decomposition is the root
    private String xsiType; // the xsi:type of the decomposition

    public String getID(){
    	return this.id;
    }
    
    public void setID(String ID){
    	this.id = ID;
    }
    
    /**
     * Create a new YAWL decomposition, given its name, whether it is the root, and its xsi:type.
     * @param id The given name
     * @param isRootNet Whether it is the root ("true") or not (anything else)
     * @param xsiType The xsi:type
     */
        
    public Decomposition(String id, String isRootNet, String xsiType) {
        setID(id);
        setRootNet(isRootNet);
        setXSIType(xsiType);
    }
    
    public void setRootNet(String rootNet){
    	this.isRootNet = rootNet.equals("true");
    }
    
    public void setXSIType(String xsiType){
    	this.xsiType = xsiType;
    }

    /**
     * Returns whether root.
     * @return Whether root.
     */
    public boolean isRoot() {
        return isRootNet;
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }
    
    public List<Edge> getEdges(){
    	if (edges == null)
			edges = new ArrayList<Edge>();
		return edges;
    }

    /**
     * Returns whether any normal edge exists form the first node to the second node.
     * @param fromNode YAWLNode The given first node.
     * @param toNode YAWLNode The given second node.
     * @return boolean Returns true if any edge from the first to the second node is a normal edge.
     */
    public boolean hasNormalEdges(Node fromNode, Node toNode) {
        HashSet<Edge> edges = this.getEdgesBetween(fromNode, toNode);
        for (Edge edge : edges) {
            if (edge.isNormal()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds an input condition with given name.
     * @param name The given name
     */
    public Condition addInputCondition(String id, String name) {
        Condition condition = new Condition(id, name, Condition.ConditionType.IN);
        nodes.put(name, condition);
        return condition;
    }

    /**
     * Adds an output condition with given name.
     * @param name The given name
     */
    public Condition addOutputCondition(String id, String name) {
        Condition condition = new Condition(id, name, Condition.ConditionType.OUT);
        nodes.put(name, condition);
        return condition;
    }

    /**
     * Adds a (normal) condition with given name.
     * @param name The given name
     */
    public Condition addCondition(String id, String name) {
        Condition condition = new Condition(id, name, Condition.ConditionType.NONE);
        nodes.put(name, condition);
        return condition;
    }

    /**
     * Adds a task with given name, join type, split type, and subdecomposition name.
     * @param id The given identifier
     * @param name The given name
     * @param join The given join type (and, xor, or)
     * @param split The given split type (and, xor, or)
     * @param decomposesTo The given subdecomposition name.
     * @return the task created.
     */
    public Task addTask(String id, String name, String join, String split,
                            String decomposesTo) {
        Task.SplitJoinType joinType = join.equals("and") ? Task.SplitJoinType.AND : join.equals("xor") ?
                       Task.SplitJoinType.XOR :
                       join.equals("or") ? Task.SplitJoinType.OR : Task.SplitJoinType.NONE;
        
        Task.SplitJoinType splitType = split.equals("and") ? Task.SplitJoinType.AND : split.equals("xor") ?
        		Task.SplitJoinType.XOR :
                        split.equals("or") ? Task.SplitJoinType.OR : Task.SplitJoinType.NONE;
        
        Task task = new Task(id, name, joinType, splitType, decomposesTo);
        nodes.put(name, task);
        return task;
    }

    public void removeYawlNode(String name) {
        nodes.remove(name);
    }

    /**
     * Adds a normal edge from the given source node to the given destination node, given whether it is a default flow, given its predicate and its ordering.
     * @param fromName The name of the source node
     * @param toName The name of the destination node
     * @param isDefaultFLow Whether it is a default edge
     * @param predicate The given predicate
     * @param ordering The given predicate ordering
     */

    public Edge addNormalEdge(Node fromNode, Node toNode, boolean isDefaultFlow, String predicate,
           int ordering) {
    	
        Edge newEdge = new Edge(fromNode, toNode, Edge.EdgeType.NORMAL, isDefaultFlow, predicate, ordering);
        addEdge(newEdge);
        return newEdge;
    }

    /**
     * Adds a reset edge from the given source node to the given destination node.
     * @param fromName The name of the source node
     * @param toName The name of the destination node
     */
    public void addResetEdge(Node fromNode, Node toNode) {
    	
    	boolean isDefaultFlow = false;
    	String predicate = "";
    	int ordering = 0;

    	Edge newEdge = new Edge(fromNode, toNode, Edge.EdgeType.RESET, isDefaultFlow, predicate, ordering); // Absence of extra parameters result in a reset edge
        addEdge(edge);
        //nodes.put(fromName, fromVertex);
        //nodes.put(toName, toVertex);
    }

    /**
     * Export to YAWL file.
     * @return String The string to export for this YAWLDecompositon.
     */
    public String writeToYAWL() {
        String s = "";
        s += "\t\t<decomposition\n";
        s += "\t\t\tid=\"" + id + "\"\n";
        if (isRootNet) {
            s += "\t\t\tisRootNet=\"true\"\n";
        }
        s += "\t\t\txsi:type=\"" + xsiType + "\"\n";
        s += "\t\t>\n";

        Iterator it = getVerticeList().iterator();
        if (it.hasNext()) {
            s += "\t\t\t<processControlElements>\n";
            for (int i = 0; i < 3; i++) {
                while (it.hasNext()) {
                    Object object = it.next();
                    if (object instanceof Task) {
                        s += ((Task) object).writeToYAWL(i);
                    } else if (object instanceof Condition) {
                        s += ((Condition) object).writeToYAWL(i);
                    }
                }
                it = getVerticeList().iterator();
            }
            s += "\t\t\t</processControlElements>\n";
        }

        s += "\t\t</decomposition>\n";
        return s;
    }
}
