package com.hsuk.video.data.dto.youtubeApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse implements Serializable {

    private String nextPageToken;
    private PageInfo pageInfo;
    private List<Items> items;

    @Override
    public String toString() {
        StringBuilder newString = new StringBuilder();
        for (Items item : this.items) {
            Map<String, String> map = new HashMap<>();
            map.put("id", item.getId());
            map.put("title", item.getSnippet().getTitle());
            map.put("description", item.getSnippet().getDescription());
            map.put("thumbnail", item.getSnippet().getThumbnails().getHigh().getUrl());
            newString.append(map.toString());
            newString.append(System.lineSeparator());
        }
        return newString.toString();
    }
}
