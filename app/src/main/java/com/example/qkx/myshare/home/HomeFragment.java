package com.example.qkx.myshare.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qkx.myshare.DividerItemDecoration;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.data.Share;

import java.util.List;

/**
 * Created by qkx on 16/5/30.
 */
public class HomeFragment extends Fragment implements HomeContact.View {

    private HomeContact.Presenter mPresenter;

    private RecyclerView recyclerView;

    private HomeAdapter mAdapter;

    @Override
    public void setPresenter(HomeContact.Presenter presenter) {
        mPresenter = presenter;
    }

    public static HomeFragment newInstance() {

        Bundle args = new Bundle();

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_detail, container, false);

        mAdapter = new HomeAdapter(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_detail);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration decoration = new DividerItemDecoration(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(decoration);

        return view;
    }

    public void refresh() {

    }

    public void refresh(List<Share> shares) {
        mAdapter.setData(shares);
        mAdapter.notifyDataSetChanged();
    }

}
