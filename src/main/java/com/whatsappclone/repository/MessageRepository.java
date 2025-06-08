package com.whatsappclone.repository;

import com.whatsappclone.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrSenderAndReceiver(
        String sender1, String receiver1, String sender2, String receiver2
    );
}