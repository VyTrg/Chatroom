package com.example.chatroom.controller;

import com.example.chatroom.repository.ConversationRepository;
import com.example.chatroom.repository.ConversationMemberRepository;
import com.example.chatroom.model.Conversation;
import com.example.chatroom.model.ConversationMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ContextMenuController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private ConversationMemberRepository conversationMemberRepository;

    @MessageMapping("/conversation-change")
    public void processConversationChange(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth == null) {
                System.err.println("Không tìm thấy thông tin xác thực cho yêu cầu thay đổi cuộc trò chuyện");
                return;
            }

            String type = (String) payload.get("type");
            String conversationIdStr = (String) payload.get("conversationId");
            String initiatorIdStr = (String) payload.get("initiatorId");
            
            // Convert String IDs to Long
            Long conversationId;
            Long initiatorId;
            
            try {
                conversationId = Long.parseLong(conversationIdStr);
                initiatorId = Long.parseLong(initiatorIdStr);
            } catch (NumberFormatException e) {
                System.err.println("Lỗi convert ID: " + e.getMessage());
                return;
            }

            System.out.println("Nhận thông báo thay đổi cuộc trò chuyện: " + type + " từ người dùng " + initiatorId + " cho cuộc trò chuyện " + conversationId);

            // Tìm tất cả người dùng trong cuộc trò chuyện
            List<ConversationMember> participants = conversationMemberRepository.findAllByConversationId(conversationId);
            
            // Lọc ra người dùng khác (không phải người gửi thông báo)
            List<String> recipientIds = participants.stream()
                .map(cm -> cm.getUser().getId().toString())  // Lấy ID người dùng và chuyển sang String
                .filter(userId -> !userId.equals(initiatorIdStr)) // Lọc ra người gửi thông báo
                .collect(Collectors.toList());
                
            System.out.println("Sẽ gửi thông báo đến " + recipientIds.size() + " người dùng");
            
            // Gửi thông báo đến tất cả người dùng
            for (String recipientId : recipientIds) {
                messagingTemplate.convertAndSendToUser(
                    recipientId,
                    "/queue/conversation-changes",
                    payload
                );
                System.out.println("Đã gửi thông báo thay đổi cuộc trò chuyện đến người dùng: " + recipientId);
            }
        } catch (Exception e) {
            System.err.println("Lỗi xử lý thay đổi cuộc trò chuyện: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/conversations/{conversationId}/edit")
    public void processMessageEdit(@DestinationVariable Long conversationId, @Payload Map<String, Object> payload) {
        try {
            System.out.println("Nhận yêu cầu chỉnh sửa tin nhắn cho cuộc trò chuyện ID: " + conversationId);
            System.out.println("Payload: " + payload);
            
            // Lấy ra senderId từ payload
            String senderId = payload.get("senderId").toString();
            
            // Tìm tất cả người dùng trong cuộc trò chuyện
            List<ConversationMember> participants = conversationMemberRepository.findAllByConversationId(conversationId);
            
            // Lọc ra người dùng khác (không phải người gửi thông báo)
            List<String> recipientIds = participants.stream()
                .map(cm -> cm.getUser().getId().toString())  // Lấy ID người dùng và chuyển sang String
                .filter(userId -> !userId.equals(senderId)) // Lọc ra người gửi thông báo
                .collect(Collectors.toList());
                
            System.out.println("Gửi thông báo xóa tin nhắn đến topic: /topic/conversations/" + conversationId + "/edit");
            System.out.println("Nội dung payload: " + payload);
            System.out.println("Sẽ gửi thông báo chỉnh sửa tin nhắn đến " + recipientIds.size() + " người dùng");
            
            // Gửi thông báo đến tất cả người dùng trong cuộc trò chuyện (ngoại trừ người gửi)
//            messagingTemplate.convertAndSend(
//                "/topic/conversations/" + conversationId + "/edit",
//                payload
//            );
            for (String recipientId : recipientIds) {
                messagingTemplate.convertAndSendToUser(
                        recipientId,
                        "/queue/message-edited",
                        payload
                );
            }
            System.out.println("Đã gửi thông báo chỉnh sửa tin nhắn thành công");
        } catch (Exception e) {
            System.err.println("Lỗi xử lý chỉnh sửa tin nhắn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/conversations/{conversationId}/delete")
    public void processMessageDelete(@DestinationVariable Long conversationId, @Payload Map<String, Object> payload) {
        try {
            System.out.println("Nhận yêu cầu xóa tin nhắn cho cuộc trò chuyện ID: " + conversationId);
            System.out.println("Payload: " + payload);
            
            // Lấy ra senderId từ payload
            String senderId = payload.get("senderId").toString();
            
            // Tìm tất cả người dùng trong cuộc trò chuyện
            List<ConversationMember> participants = conversationMemberRepository.findAllByConversationId(conversationId);
            
            // Lọc ra người dùng khác (không phải người gửi thông báo)
            List<String> recipientIds = participants.stream()
                .map(cm -> cm.getUser().getId().toString())  // Lấy ID người dùng và chuyển sang String
                .filter(userId -> !userId.equals(senderId)) // Lọc ra người gửi thông báo
                .collect(Collectors.toList());
                
            System.out.println("Sẽ gửi thông báo xóa tin nhắn đến " + recipientIds.size() + " người dùng");

            for (String recipientId : recipientIds) {
                messagingTemplate.convertAndSendToUser(
                        recipientId,
                        "/queue/message-deleted",
                        payload
                );
            }
            System.out.println("Đã gửi thông báo xóa tin nhắn thành công");
        } catch (Exception e) {
            System.err.println("Lỗi xử lý xóa tin nhắn: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/discussions")
    public void handleDiscussionUpdates(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth == null) {
                System.err.println("Không tìm thấy thông tin xác thực cho yêu cầu cập nhật cuộc trò chuyện");
                return;
            }
            
            String type = (String) payload.get("type");
            String initiatorIdStr = (String) payload.get("initiatorId");
            String conversationIdStr = (String) payload.get("conversationId");
            
            System.out.println("Nhận thông báo cập nhật cuộc trò chuyện: " + type + " từ người dùng " + initiatorIdStr);
            
            if (conversationIdStr != null) {
                Long conversationId = Long.parseLong(conversationIdStr);
                // Tìm tất cả thành viên trong cuộc trò chuyện
                List<ConversationMember> members = conversationMemberRepository.findAllByConversationId(conversationId);
                
                // Gửi thông báo đến tất cả thành viên (trừ người gửi)
                for (ConversationMember member : members) {
                    String userId = member.getUser().getId().toString();
                    if (!userId.equals(initiatorIdStr)) {
                        messagingTemplate.convertAndSendToUser(
                            userId,
                            "/queue/discussions",
                            payload
                        );
                        System.out.println("Đã gửi thông báo cập nhật cuộc trò chuyện đến người dùng: " + userId);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi xử lý cập nhật cuộc trò chuyện: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @MessageMapping("/block-contact")
    public void handleBlockContact(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth == null) {
                System.err.println("Không tìm thấy thông tin xác thực cho yêu cầu chặn liên hệ");
                return;
            }
            
            String initiatorIdStr = (String) payload.get("initiatorId");
            String targetIdStr = (String) payload.get("targetId");
            
            System.out.println("Nhận thông báo chặn liên hệ từ người dùng " + initiatorIdStr + " đến người dùng " + targetIdStr);
            
            // Gửi thông báo đến người bị chặn
            if (targetIdStr != null) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "BLOCK");
                notification.put("initiatorId", initiatorIdStr);
                notification.put("timestamp", System.currentTimeMillis());
                
                messagingTemplate.convertAndSendToUser(
                    targetIdStr,
                    "/queue/contact-changes",
                    notification
                );
                System.out.println("Đã gửi thông báo chặn liên hệ đến người dùng: " + targetIdStr);
                
                // Gửi xác nhận cho người thực hiện chặn
                Map<String, Object> confirmation = new HashMap<>();
                confirmation.put("type", "BLOCK_CONFIRMATION");
                confirmation.put("targetId", targetIdStr);
                confirmation.put("status", "SUCCESS");
                confirmation.put("timestamp", System.currentTimeMillis());
                
                messagingTemplate.convertAndSendToUser(
                    initiatorIdStr,
                    "/queue/contact-changes",
                    confirmation
                );
            }
        } catch (Exception e) {
            System.err.println("Lỗi xử lý chặn liên hệ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @MessageMapping("/notify-contact-deleted")
    public void handleDeleteContact(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth == null) {
                System.err.println("Không tìm thấy thông tin xác thực cho yêu cầu xóa liên hệ");
                return;
            }
            
            String initiatorIdStr = (String) payload.get("initiatorId");
            String targetIdStr = (String) payload.get("targetId");
            String conversationIdStr = (String) payload.get("conversationId");
            Boolean isGroup = (Boolean) payload.getOrDefault("isGroup", false);
            
            System.out.println("Nhận thông báo xóa liên hệ từ người dùng " + initiatorIdStr);
            
            if (isGroup && conversationIdStr != null) {
                // Nếu là nhóm, gửi thông báo đến tất cả thành viên
                Long conversationId = Long.parseLong(conversationIdStr);
                List<ConversationMember> members = conversationMemberRepository.findAllByConversationId(conversationId);
                
                for (ConversationMember member : members) {
                    String userId = member.getUser().getId().toString();
                    if (!userId.equals(initiatorIdStr)) {
                        messagingTemplate.convertAndSendToUser(
                            userId,
                            "/queue/contact-changes",
                            payload
                        );
                        System.out.println("Đã gửi thông báo xóa nhóm đến thành viên: " + userId);
                    }
                }
            } else if (targetIdStr != null) {
                // Nếu là liên hệ cá nhân, gửi thông báo đến người liên hệ
                messagingTemplate.convertAndSendToUser(
                    targetIdStr,
                    "/queue/contact-changes",
                    payload
                );
                System.out.println("Đã gửi thông báo xóa liên hệ đến người dùng: " + targetIdStr);
            }
            
            // Gửi xác nhận cho người thực hiện xóa
            Map<String, Object> confirmation = new HashMap<>();
            confirmation.put("type", "DELETE_CONFIRMATION");
            confirmation.put("targetId", targetIdStr);
            confirmation.put("conversationId", conversationIdStr);
            confirmation.put("isGroup", isGroup);
            confirmation.put("status", "SUCCESS");
            confirmation.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSendToUser(
                initiatorIdStr,
                "/queue/contact-changes",
                confirmation
            );
        } catch (Exception e) {
            System.err.println("Lỗi xử lý xóa liên hệ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @MessageMapping("/notifications")
    public void handleNotifications(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth == null) {
                System.err.println("Không tìm thấy thông tin xác thực cho yêu cầu gửi thông báo");
                return;
            }
            
            String type = (String) payload.get("type");
            String senderId = (String) payload.get("senderId");
            String recipientId = (String) payload.get("recipientId");
            
            System.out.println("Nhận yêu cầu gửi thông báo loại " + type + " từ người dùng " + senderId + " đến " + recipientId);
            
            // Gửi thông báo đến người nhận
            if (recipientId != null) {
                messagingTemplate.convertAndSendToUser(
                    recipientId,
                    "/queue/notifications",
                    payload
                );
                System.out.println("Đã gửi thông báo đến người dùng: " + recipientId);
            }
        } catch (Exception e) {
            System.err.println("Lỗi xử lý gửi thông báo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
