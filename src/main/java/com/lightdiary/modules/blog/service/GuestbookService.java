package com.lightdiary.modules.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lightdiary.modules.blog.dto.GuestbookParam;
import com.lightdiary.modules.blog.model.Guestbook;
import com.lightdiary.modules.blog.vo.GuestbookVO;

import java.util.List;

public interface GuestbookService extends IService<Guestbook> {

    List<GuestbookVO> listPublic(Integer pageNum, Integer pageSize);

    long countPublic();

    boolean submit(GuestbookParam param, String ip, String userAgent);

    boolean like(Long id, String ip);

    int getRemainingDaily(String ip);

    Page<Guestbook> listAdmin(Integer pageNum, Integer pageSize, String status);

    boolean approve(Long id);

    boolean reject(Long id);

    long countApproved();
}
