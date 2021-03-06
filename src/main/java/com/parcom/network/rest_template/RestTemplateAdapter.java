package com.parcom.network.rest_template;

import com.parcom.exceptions.RPCParcomException;
import com.parcom.network.Network;
import com.parcom.network.Service;
import com.parcom.network.ServicesProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;



@RequiredArgsConstructor
@Slf4j
public class RestTemplateAdapter implements Network {

    private final ServicesProps servicesProps;
    private  final RestTemplate restTemplate;

    private UriComponentsBuilder getUriBuilder(String serviceName) {
        Service service = servicesProps.getByName(serviceName);
        return  UriComponentsBuilder.newInstance()
                .scheme(RestTemplateUtils.SCHEME).
                        host(service.getHost()).
                        port(service.getPort()).
                        pathSegment(RestTemplateUtils.API,service.getVersion());
    }



    @Override
     public  <T,B> ResponseEntity<T> call(String serviceName,
                                          HttpMethod method,
                                          Class<T> responseType,
                                          @Nullable B body,
                                          Map<String, String> additionalHeaders,
                                          MultiValueMap<String, String> queryParams,
                                          String... pathSegments
     )
     {
        URI uri = getUriBuilder(serviceName).queryParams(queryParams).pathSegment(pathSegments).build().toUri();


        HttpHeaders httpHeaders = RestTemplateUtils.getHttpHeaders();

        if (additionalHeaders != null)
            additionalHeaders.forEach(httpHeaders::set);


        HttpEntity<B> httpEntity = new HttpEntity<>(body, httpHeaders);
        log.info("Call {}",uri.toString());
        ResponseEntity<T> responseEntity= restTemplate.exchange(uri,method,httpEntity,responseType);
        if (responseEntity.getStatusCode()== HttpStatus.OK) {
            return responseEntity;
        }
        else
        {
            throw new RPCParcomException();
        }
    }

}
