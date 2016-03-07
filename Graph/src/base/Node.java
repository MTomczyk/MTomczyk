package base;

import java.util.ArrayList;

public class Node
{
    protected ArrayList<Edge> _edges = null;

    public Node()
    {
        this(2);
    }

    public Node(Integer size)
    {
        this._edges = new ArrayList<>(size);
    }

    @SuppressWarnings("unused")
    public Node(ArrayList<Edge> edge)
    {
        this._edges = edge;
    }

    public void addEdge(Edge edge)
    {
        if (edge != null)
        {
            this._edges.add(edge);
        }
    }

    @SuppressWarnings("unused")
    public void removeEdge(Integer index)
    {
        if ((_edges != null) && (index < _edges.size()))
        {
            this._edges.remove((int)index);
        }
    }

    @SuppressWarnings("unused")
    public void removeEdge(Edge object)
    {
        if (_edges != null)
        {
            this._edges.remove(object);
        }
    }

    @SuppressWarnings("unused")
    public ArrayList<Edge> getEdges()
    {
        return _edges;
    }

    @SuppressWarnings("unused")
    public void setEdges(ArrayList<Edge> edges)
    {
        this._edges = edges;
    }

}
