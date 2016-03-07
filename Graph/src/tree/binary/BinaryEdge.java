package tree.binary;


import base.Edge;

public class BinaryEdge extends Edge
{
	public BinaryEdge()
	{
		this._nodes = new BinaryNode[2];
	}

	public BinaryNode getParent()
	{
		if ((_nodes != null) && (_nodes.length > 0))
		{
			return (BinaryNode) this._nodes[0];
		}
		return null;
	}

	public void setParent(BinaryNode node)
	{
		//if (node != null)
		//{
			this._nodes[0] = node;
		//}
	}

	@SuppressWarnings("unused")
	public BinaryNode getChildren()
	{
		if ((_nodes != null) && (_nodes.length > 0))
		{
			return (BinaryNode) this._nodes[1];
		}
		return null;
	}

	@SuppressWarnings("unused")
	public void setChildren(BinaryNode node)
	{
		//if (node != null)
		//{
			this._nodes[1] = node;
		//}
	}

}
