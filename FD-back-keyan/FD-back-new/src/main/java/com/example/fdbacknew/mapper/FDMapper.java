package com.example.fdbacknew.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FDMapper {

    void addConversation( String conversationId, String historyPath,
                           int createTime);

    @Select("select history_path from conversations where conversation_id = #{conversationId}")
    String getHistoryPath(String conversationId);

    @Select("select count(*) from conversations where conversation_id = #{conversationId}")
    int getConversationCount(String conversationId);

    @Select("select create_time from conversations where conversation_id = #{conversationId}")
    int getCreateTime(String conversationId);
}
