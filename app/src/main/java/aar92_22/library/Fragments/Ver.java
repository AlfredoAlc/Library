package aar92_22.library.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import aar92_22.library.BaseDeDatos.Libro;
import aar92_22.library.R;
import aar92_22.library.Utilities.FragmentLink;
import aar92_22.library.Utilities.Holder;

import static android.view.View.INVISIBLE;


public class Ver extends Fragment implements FragmentLink {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private List<Libro> bookList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ChoosenBook choosen;


    public Ver() {
    }

    public static Ver newInstance(Holder param1, String param2) {
        Ver fragment = new Ver();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookList = ((Holder) getArguments().getSerializable(ARG_PARAM1)).getBookList();
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ver, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.libros);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new LibrosAdapter(bookList, getContext(), this);
        mRecyclerView.setAdapter(mAdapter);



        return view;
    }




    @Override
    public void linkPosition(int pos) {
        choosen.sendBook(bookList.get(pos));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChoosenBook) {
            choosen = (ChoosenBook) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        choosen = null;
    }

    public interface ChoosenBook {
        public void sendBook(Libro libro);
    }

}
