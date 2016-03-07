package tree.binary;


import extractor.interfaces.IValueExtractor;
import standard.Common;
import tree.Tree;

import java.util.HashMap;
import java.util.Map;

public class BinaryTree<T> extends Tree
{
    private Map<T, BinaryNode> _map = null;
    private Map<BinaryNode, T> _rMap = null;

    private BinaryNode _root = null;
    public IValueExtractor _valueExtractor = null;

    private boolean _direction = true;

    private BinaryNode _sNode = null;

    private double _maxValue = Common.MIN_DOUBLE;
    private double _minValue = Common.MAX_DOUBLE;

    public BinaryTree(IValueExtractor valueExtractor)
    {
        this(true, valueExtractor);
    }

    public BinaryTree(boolean direction, IValueExtractor valueExtractor)
    {
        this._direction = direction;
        this._valueExtractor = valueExtractor;

        this._map = new HashMap<>();
        this._rMap = new HashMap<>();
    }

    // TODO REBUILD TREE :)


    // TODO REMOVE ELEMENT

    private void updateBounds()
    {
        if (_root == null)
        {
            _maxValue = Common.MIN_DOUBLE;
            _minValue = Common.MAX_DOUBLE;
            return;
        }

        BinaryNode l = dropDownLeft(_root);
        BinaryNode r = dropDownRight(_root);

        if (l != null)
        {
            Double lv = _valueExtractor.getValue(_rMap.get(l));
            _minValue = lv;
        } else _minValue = _valueExtractor.getValue(_rMap.get(_root));

        if (r != null)
        {
            Double rv = _valueExtractor.getValue(_rMap.get(r));
            _maxValue = rv;
        } else _maxValue = _valueExtractor.getValue(_rMap.get(_root));

    }

    public HashMap<T, HashMap<T, Integer>> getMatrixRepresentation()
    {
        HashMap<T, HashMap<T, Integer>> m = new HashMap<>();

        for (Map.Entry<BinaryNode, T> entry : _rMap.entrySet())
        {
            BinaryNode key = entry.getKey();
            T o = entry.getValue();
            m.put(o, new HashMap<>());

            if (key.getLeftChildren() != null)
            {
                T v = _rMap.get(key.getLeftChildren());
                m.get(o).put(v, 1);
            }

            if (key.getRightChildren() != null)
            {
                T v = _rMap.get(key.getRightChildren());
                m.get(o).put(v, 1);
            }

            if (key.getParent() != null)
            {
                T v = _rMap.get(key.getParent());
                m.get(o).put(v, -1);
            }
        }

        return m;
    }

    public void remove(T object)
    {
        if (_map.get(object) == null) return;
        BinaryNode bn = _map.get(object);

        // IS A LEAF
        if ((bn.getLeftChildren() == null) && (bn.getRightChildren() == null))
        {
            BinaryNode parent = bn.getParent();
            if (parent != null)
            {
                BinaryNode lp = parent.getLeftChildren();
                if (lp == bn)
                    parent.setLeftChildren(null);
                else parent.setRightChildren(null);

            } else _root = null;
        }
        // ONE CHILDREN
        else if ((bn.getLeftChildren() == null) || (bn.getRightChildren() == null))
        {
            BinaryNode lc = bn.getLeftChildren();
            BinaryNode rc = bn.getRightChildren();
            BinaryNode node = lc;
            if (lc == null) node = rc;
            BinaryNode parent = bn.getParent();
            if (parent != null)
            {
                BinaryNode lp = parent.getLeftChildren();
                if (lp == bn)
                    parent.setLeftChildren(node);
                else parent.setRightChildren(node);
                node.setParent(parent);
            } else _root = node;
        }
        // BOTH CHILDREN
        else
        {
            BinaryNode rc = bn.getRightChildren();
            BinaryNode min = dropDownLeft(rc);
            BinaryNode uP = bn.getParent();
            if (min == rc)
            {
                rc.setLeftChildren(bn.getLeftChildren());
                bn.getLeftChildren().setParent(rc);

                if (uP == null) _root = rc;
                else
                {
                    rc.setParent(bn.getParent());
                    if (bn.getParent().getLeftChildren() == bn)
                        bn.getParent().setLeftChildren(rc);
                    else
                        bn.getParent().setRightChildren(rc);
                }


            } else
            {
                BinaryNode rMin = min.getRightChildren();

                if (rMin != null)
                {
                    rMin.setParent(min.getParent());
                    min.getParent().setLeftChildren(rMin);
                }

                min.setLeftChildren(bn.getLeftChildren());
                min.setRightChildren(bn.getRightChildren());
                bn.getRightChildren().setParent(min);
                bn.getLeftChildren().setParent(min);

                if (uP == null)
                {
                    _root = min;
                    min.setParent(null);
                } else
                {
                    min.setParent(bn.getParent());
                    if (bn.getParent().getLeftChildren() == bn)
                        bn.getParent().setLeftChildren(min);
                    else
                        bn.getParent().setRightChildren(min);
                }


            }
        }

        _map.remove(object);
        _rMap.remove(bn);
        _size--;
        updateBounds();
    }

    public void insert(T object)
    {
        BinaryNode node = new BinaryNode();
        _map.put(object, node);
        _rMap.put(node, object);

        _size++;

        if (_root == null)
        {
            _root = node;
            _root.setParent(null);
            _root.setLeftChildren(null);
            _root.setRightChildren(null);

            Double a = _valueExtractor.getValue(object);
            if (a < this._minValue) _minValue = a;
            if (a > this._maxValue) _maxValue = a;

        } else
        {
            BinaryNode cNode = _root;
            BinaryNode children = null;
            boolean right;
            do
            {
                if (children != null)
                {
                    cNode = children;
                }

                Double a = _valueExtractor.getValue(object);
                if (this._rMap.get(cNode) == null) print();
                Double b = _valueExtractor.getValue(this._rMap.get(cNode));

                if (a < this._minValue) _minValue = a;
                if (a > this._maxValue) _maxValue = a;

                if (((a >= b) && (_direction)) || ((a < b) && (!_direction)))
                {
                    right = true;
                    children = cNode.getRightChildren();
                } else
                {
                    right = false;
                    children = cNode.getLeftChildren();
                }

            } while (children != null);

            node.setParent(cNode);
            node.setLeftChildren(null);
            node.setRightChildren(null);

            if (right) cNode.setRightChildren(node);
            else cNode.setLeftChildren(node);

        }
    }

    public void print()
    {
        if (_root != null)
        {
            System.out.println("ROOT ---------------------");
            BinaryNode key = _root;
            T value = _rMap.get(_root);

            String s = String.format("Node: %.8f | ", _valueExtractor.getValue(value));

            BinaryNode parent = key.getParent();
            if (parent != null)
                s += String.format("Parent %.8f ", _valueExtractor.getValue(_rMap.get(parent)));
            else
                s += "Parent: NULL ";

            BinaryNode left = key.getLeftChildren();
            if (left != null)
                s += String.format("LeftC %.8f ", _valueExtractor.getValue(_rMap.get(left)));
            else
                s += "LeftC: NULL ";

            BinaryNode right = key.getRightChildren();
            if (right != null)
                s += String.format("RightC %.8f ", _valueExtractor.getValue(_rMap.get(right)));
            else
                s += "RightC: NULL ";

            System.out.println(s);
            System.out.println("--------------------------");
        }

        for (Map.Entry<BinaryNode, T> entry : _rMap.entrySet())
        {
            BinaryNode key = entry.getKey();
            T value = entry.getValue();

            String s = String.format("Node: %.8f | ", _valueExtractor.getValue(value));

            BinaryNode parent = key.getParent();
            if (parent != null)
                s += String.format("Parent %.8f ", _valueExtractor.getValue(_rMap.get(parent)));
            else
                s += "Parent: NULL ";

            BinaryNode left = key.getLeftChildren();
            if (left != null)
                s += String.format("LeftC %.8f ", _valueExtractor.getValue(_rMap.get(left)));
            else
                s += "LeftC: NULL ";

            BinaryNode right = key.getRightChildren();
            if (right != null)
                s += String.format("RightC %.8f ", _valueExtractor.getValue(_rMap.get(right)));
            else
                s += "RightC: NULL ";

            System.out.println(s);
        }
    }

    public void clear()
    {
        this._size = 0;
        this._root = null;
        this._map = new HashMap<>();
        this._rMap = new HashMap<>();
    }

    // --- BINARY SEARCH ----------

    public void setSearch(T object)
    {
        this._sNode = this._map.get(object);
    }

    public T search()
    {
        _sNode = _root;
        if (_sNode != null)
        {
            _sNode = this.dropDownLeft(_sNode);
            return this._rMap.get(_sNode);
        } else return null;
    }

    private BinaryNode dropDownLeft(BinaryNode start)
    {
        while (start.getLeftChildren() != null)
        {
            start = start.getLeftChildren();
        }
        return start;
    }

    private BinaryNode dropDownRight(BinaryNode start)
    {
        while (start.getRightChildren() != null)
        {
            start = start.getRightChildren();
        }
        return start;
    }

    public T next()
    {
        BinaryNode parent;
        boolean firstMove = true;

        while (true)
        {
            if (firstMove)
            {
                if (_sNode.getRightChildren() != null)
                {
                    _sNode = this.dropDownLeft(_sNode.getRightChildren());
                    break;
                }

            }

            firstMove = false;

            parent = _sNode.getParent();
            if (parent == null) return null;

            if ((parent.getLeftChildren() != null) && (parent.getLeftChildren().equals(_sNode)))
            {
                _sNode = parent;
                break;
            }

            if ((parent.getRightChildren() != null) && (parent.getRightChildren().equals(_sNode)))
            {
                _sNode = parent;
            } else
            {
                _sNode = parent;
                break;
            }
        }

        return this._rMap.get(_sNode);
    }

    public T previous()
    {
        BinaryNode parent;
        boolean firstMove = true;

        while (true)
        {
            if (firstMove)
            {
                if (_sNode.getLeftChildren() != null)
                {
                    _sNode = this.dropDownRight(_sNode.getLeftChildren());
                    break;
                }

            }

            firstMove = false;

            parent = _sNode.getParent();
            if (parent == null) return null;

            if ((parent.getRightChildren() != null) && (parent.getRightChildren().equals(_sNode)))
            {
                _sNode = parent;
                break;
            }

            if ((parent.getLeftChildren() != null) && (parent.getLeftChildren().equals(_sNode)))
            {
                _sNode = parent;
            } else
            {
                _sNode = parent;
                break;
            }
        }

        return this._rMap.get(_sNode);
    }

    @SuppressWarnings("unused")
    public boolean isDirection()
    {
        return _direction;
    }

    public void setDirection(boolean direction)
    {
        this._direction = direction;
    }

    @SuppressWarnings("unused")
    public double getMaxValue()
    {
        return _maxValue;
    }

    @SuppressWarnings("unused")
    public void setMaxValue(double maxValue)
    {
        this._maxValue = maxValue;
    }

    @SuppressWarnings("unused")
    public double getMinValue()
    {
        return _minValue;
    }

    @SuppressWarnings("unused")
    public void setMinValue(double minValue)
    {
        this._minValue = minValue;
    }

}
