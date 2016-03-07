package tree.binary;

import base.Node;
import base.Edge;

import java.util.ArrayList;

public class BinaryNode extends Node
{
	public BinaryNode()
	{
		this._edges = new ArrayList<>(3);
		this.addEdge(new BinaryEdge());
		this.addEdge(new BinaryEdge());
		this.addEdge(new BinaryEdge());
	}

	public BinaryNode getParent()
	{
		BinaryEdge e = this.getBinaryEdge(0);
		if (e != null)
		{
			return e.getParent();
		}
		return null;
	}

	public void setParent(BinaryNode parent)
	{
		BinaryEdge e = this.getBinaryEdge(0);
		if (e != null)
		{
			e.setParent(parent);
		}
	}

	public BinaryNode getLeftChildren()
	{
		BinaryEdge e = this.getBinaryEdge(1);
		if (e != null)
		{
			return e.getParent();
		}
		return null;
	}

	public void setLeftChildren(BinaryNode children)
	{
		BinaryEdge e = this.getBinaryEdge(1);
		if (e != null)
		{
			e.setParent(children);
		}
	}

	public BinaryNode getRightChildren()
	{
		BinaryEdge e = this.getBinaryEdge(2);
		if (e != null)
		{
			return e.getParent();
		}
		return null;
	}

	public void setRightChildren(BinaryNode children)
	{
		BinaryEdge e = this.getBinaryEdge(2);
		if (e != null)
		{
			e.setParent(children);
		}
	}

	public BinaryEdge getBinaryEdge(Integer index)
	{
		if ((_edges != null) && (_edges.size() >= index))
		{
			Object o = this._edges.get(index);
			if (o == null) return null;
			return (BinaryEdge) this._edges.get(index);
		}
		return null;
	}

	@Override
	public void addEdge(Edge edge)
	{
		if (this._edges.size() < 4)
		{
			this._edges.add(edge);
		}
	}

	@Override
	public void setEdges(ArrayList<Edge> edges)
	{
		if ((edges != null) && (edges.size() > 0) && (edges.size() < 4))
		{
			this._edges = edges;
		}
		else if (edges == null)
		{
			this._edges = null;
		}
	}
}
