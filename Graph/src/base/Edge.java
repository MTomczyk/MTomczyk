package base;

public class Edge
{
    protected Node _nodes[] = null;

    public Edge()
    {
        this._nodes = new Node[2];
    }

    @SuppressWarnings("unused")
    public Node[] getNodes()
    {
        return _nodes;
    }

    @SuppressWarnings("unused")
    public void setNodes(Node _nodes[])
    {
        if (_nodes.length < 3)
        {
            this._nodes = _nodes;
        }
    }
}
