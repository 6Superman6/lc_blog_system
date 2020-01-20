package demo.service;

import demo.model.Comment;

import java.util.List;

public interface CommentService {

    // 根据博客id获取该博客的所有评论
    List<Comment> listCommentByBlogId(Long blogId);

    // 添加评论
    Comment saveComment(Comment comment);

    // 删除评论
    void deleteComment(Long id);

}
