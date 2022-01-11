package xyz.fivemillion.bulletinboardapi.config.web;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_OFFSET_VALUE;
import static xyz.fivemillion.bulletinboardapi.config.web.PageRequest.DEFAULT_SIZE_VALUE;

public class PageRequestHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        String offsetStr = webRequest.getParameter("offset");
        String sizeStr = webRequest.getParameter("size");

        Long offset = DEFAULT_OFFSET_VALUE;
        Long size = DEFAULT_SIZE_VALUE;

        if (offsetStr != null && Long.parseLong(offsetStr) > 0)
            offset = Long.parseLong(offsetStr);

        if (sizeStr != null && Long.parseLong(sizeStr) > 0)
            size = Long.parseLong(sizeStr);

        return new PageRequest(offset, size);
    }
}
