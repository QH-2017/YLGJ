package com.ylgj.service.impl;

import com.ylgj.pojo.Member;
import com.ylgj.mapper.MemberMapper;
import com.ylgj.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lixin
 * @since 2026-07-04
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

}
