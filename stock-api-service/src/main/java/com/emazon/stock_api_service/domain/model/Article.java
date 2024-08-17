package com.emazon.stock_api_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//we could create the getters, setters, constructors on our own
//but lombock is very helpful here
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private long id;
    private String name;
    private String description;
}
