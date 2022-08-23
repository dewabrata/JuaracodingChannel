package com.juaracoding.youtube.connection.responses;

import com.juaracoding.youtube.model.SearchItemModel;

import java.io.Serializable;
import java.util.List;

public class ResponseSearchVideo implements Serializable {
    public String nextPageToken;
    public List<SearchItemModel> items;
}
