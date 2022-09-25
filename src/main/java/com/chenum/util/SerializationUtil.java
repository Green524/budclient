package com.chenum.util;

import com.chenum.model.PageData;
import com.chenum.model.ResultWrap;
import com.chenum.po.Article;
import com.fasterxml.jackson.core.type.TypeReference;

public class SerializationUtil {

    public <E> ResultWrap<PageData<Article>> serialization(String content, TypeReference<ResultWrap<PageData<Article>>> typeReference){
        return JsonUtil.jsonToObject(content, typeReference);
    }
}
