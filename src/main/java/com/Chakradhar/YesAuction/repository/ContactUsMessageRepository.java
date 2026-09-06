package com.Chakradhar.YesAuction.repository;

import com.Chakradhar.YesAuction.entity.ContactUsMessage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactUsMessageRepository extends JpaRepository<ContactUsMessage, Long>{
	List<ContactUsMessage> findAllByOrderByCreatedAtDesc();
}
	