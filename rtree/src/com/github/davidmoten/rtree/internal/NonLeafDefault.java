package com.github.davidmoten.rtree.internal;

import java.util.List;

import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.rtree.Context;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.Node;
import com.github.davidmoten.rtree.NonLeaf;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;

import org.model.BlockStore;
import org.model.DBContext;
import rx.Subscriber;
import rx.functions.Func1;

public final class NonLeafDefault<T, S extends Geometry> extends NonLeaf<T, S> {

    private final List<? extends Node<T, S>> children;
    private final Rectangle mbr;
    private final Context<T, S> context;


    public NonLeafDefault(List<? extends Node<T, S>> children, Context<T, S> context, DBContext dbcontext, BlockStore bs) {
        Preconditions.checkArgument(!children.isEmpty());
        this.context = context;
        this.children = children;
        this.mbr = Util.mbr(children);
        // this.id = bs.getNewBlockID(dbcontext);
        // bs.putObject(id, this);
    }

    @Override
    public Geometry geometry() {
        return mbr;
    }

    @Override
    public void searchWithoutBackpressure(Func1<? super Geometry, Boolean> criterion,
            Subscriber<? super Entry<T, S>> subscriber) {
        NonLeafHelper.search(criterion, subscriber, this);
    }

    @Override
    public int count() {
        return children.size();
    }

    @Override
    public List<Node<T, S>> add(Entry<? extends T, ? extends S> entry,DBContext dbcontext, BlockStore bs) {
        return NonLeafHelper.add(entry, this,dbcontext,bs);
    }

    @Override
    public NodeAndEntries<T, S> delete(Entry<? extends T, ? extends S> entry, boolean all,DBContext dbcontext, BlockStore bs ) {
        return NonLeafHelper.delete(entry, all, this, dbcontext,bs);
    }

    @Override
    public Context<T, S> context() {
        return context;
    }

    @Override
    public Node<T, S> child(int i) {
        return children.get(i);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Node<T, S>> children() {
        return (List<Node<T, S>>) children;
    }

    @Override
    public void update(BlockStore bs){ bs.putObject(id, this);}

}