package com.nameless.storage_server.repository;

import com.nameless.storage_server.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TokenRepository  extends JpaRepository<Token, Long> {


}