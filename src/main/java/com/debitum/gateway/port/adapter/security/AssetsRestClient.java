package com.debitum.gateway.port.adapter.security;

import com.debitum.gateway.domain.model.user.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("assets")
interface AssetsRestClient {

    @RequestMapping(value = "/assets/api/private/login/{login}/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    User findOneByLogin(@PathVariable("login") String login);
}
