
package fragtreealigner.domainobjects.graphs;

@SuppressWarnings("serial")
public class AlignmentResTreeNode extends TreeNode<AlignmentResTreeNode, AlignmentResTreeEdge> {
	private AlignmentTreeNode correspondingNode1;
	private AlignmentTreeNode correspondingNode2;
	private float score;
	private char flag;
	
	public AlignmentResTreeNode() {
		super();
	}
	
	public AlignmentResTreeNode(String label) {
		super(label);
	}
	
	public AlignmentResTreeNode(String label, AlignmentTreeNode correspondingNode1, AlignmentTreeNode correspondingNode2, float score) {
		super(label);
		this.correspondingNode1 = correspondingNode1;
		this.correspondingNode2 = correspondingNode2;
		this.score = score;
	}

	public AlignmentResTreeNode(String label, AlignmentTreeNode correspondingNode1, AlignmentTreeNode correspondingNode2, float score, char flag) {
		this(label, correspondingNode1, correspondingNode2, score);
		this.setFlag(flag);
	}

	@Override
	public void setContent(AlignmentResTreeNode node) {
		super.setContent(node);
		this.correspondingNode1 = node.getCorrespondingNode1();
		this.correspondingNode2 = node.getCorrespondingNode2();
		this.score = node.getScore();
		this.flag = node.getFlag();
	}
	
	public AlignmentTreeNode getCorrespondingNode1() {
		return correspondingNode1;
	}

	public AlignmentTreeNode getCorrespondingNode2() {
		return correspondingNode2;
	}

	public float getScore() {
		return score;
	}

	public void setFlag(char flag) {
		this.flag = flag;
	}

	public char getFlag() {
		return flag;
	}

	@Override
	public String dotParams() {
		float MAX_SCORE = 20;
		float NUM_COLORS = 8;
		int color = (int)(Math.abs(score) / (MAX_SCORE / NUM_COLORS)) + 1;
		String dotParams = super.dotParams();
		dotParams += ", style=filled, width=1";
		dotParams += ", colorscheme=" + ((score < 0) ? "reds9" : "greens9");
		dotParams += ", fillcolor=" + Integer.toString(color);
		return dotParams;
	}

	@Override
	public String toString() {
		String output = "";
		if (getFlag() == 'c') output += "c"; 
		output += "[";
		output += ((correspondingNode1 == null) || (correspondingNode1.getNeutralLoss() == null)) ? "-" : correspondingNode1.getNeutralLoss().getMolecularFormula().toString();
		output += ",";
		output += ((correspondingNode2 == null) || (correspondingNode2.getNeutralLoss() == null)) ? "-" : correspondingNode2.getNeutralLoss().getMolecularFormula().toString();
		output += "]\\n" + score;
		return output;
	}
	
	@Override
	public AlignmentResTreeNode clone() {
		AlignmentResTreeNode clonedAligResTreeNode = new AlignmentResTreeNode(new String(label), correspondingNode1, correspondingNode2, score, flag);
		return clonedAligResTreeNode;
	}

}
