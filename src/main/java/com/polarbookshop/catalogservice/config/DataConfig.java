package com.polarbookshop.catalogservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@Configuration  //이 클래스는 Spring 설정의 소스임을 나타냄
@EnableJdbcAuditing //모든 persistence entity에 대한 Auditing을 활성화 -> 활성화되면, 데이터의 생성, 변경, 삭제가 일어날 때마다 Auditing Event가 생성
//Spring Data JPA에서는 @EnableJpaAuditing을 여기에 설정하고 Entity Class에 별도로 @EntityListeners(AuditingEntityListener.class) annotation을 추가해야만 한다.
public class DataConfig {
}
