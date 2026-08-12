package com.ylgj.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylgj.commons.MessageConstant;
import com.ylgj.commons.PageResult;
import com.ylgj.commons.QueryPageBean;
import com.ylgj.commons.Result;
import com.ylgj.pojo.Setmeal;
import com.ylgj.pojo.SetmealCheckgroup;
import com.ylgj.service.SetmealCheckgroupService;
import com.ylgj.service.SetmealService;
import com.ylgj.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealCheckgroupService setmealCheckgroupService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${upload.path:${user.home}/ylgj-uploads/}")
    private String uploadPath;

    @PostMapping("/upload.do")
    public Result upload(@RequestParam("imgFile") MultipartFile file) {
        try {
            // 文件为空校验
            if (file.isEmpty()) {
                return new Result(false, "上传文件不能为空");
            }
            // 创建上传目录
            String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
            File dir = new File(uploadPath + "setmeal/" + dateDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
                if (List.of("jpg", "jpeg", "png", "gif").contains(ext)) {
                    suffix = "." + ext;
                }
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;
            // 保存文件
            File dest = new File(dir, fileName);
            file.transferTo(dest);
            // 返回图片访问路径
            String imgUrl = "/uploads/setmeal/" + dateDir + "/" + fileName;
            Map<String, Object> data = new HashMap<>();
            data.put("url", imgUrl);
            return new Result(true, MessageConstant.PIC_UPLOAD_SUCCESS, data);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
    }

    @PostMapping("/add")
    @Transactional
    public Result add(@RequestBody Setmeal setmeal) {
        try {
            // 新增时清除id，确保数据库自增生成
            setmeal.setId(null);
            // 保存套餐基本信息
            setmealService.save(setmeal);
            // 保存套餐与检查组的关联关系
            saveSetmealCheckgroup(setmeal);
            // 刷新 Redis 缓存
            redisUtil.delete(RedisUtil.SETMEAL_LIST_KEY);
            return new Result(true, MessageConstant.ADD_SETMEAL_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    @PostMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean) {
        Page<Setmeal> pageParam = new Page<>(queryPageBean.getCurrentPage(), queryPageBean.getPageSize());
        QueryWrapper<Setmeal> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (queryPageBean.getQueryString() != null && !queryPageBean.getQueryString().equals("")) {
            queryWrapper.like("code", queryPageBean.getQueryString());
            queryWrapper.or();
            queryWrapper.like("name", queryPageBean.getQueryString());
            queryWrapper.or();
            queryWrapper.like("help_code", queryPageBean.getQueryString());
        }
        Page<Setmeal> page = setmealService.page(pageParam, queryWrapper);
        // 为每个套餐查询关联的检查组
        for (Setmeal s : page.getRecords()) {
            QueryWrapper<SetmealCheckgroup> scQuery = new QueryWrapper<>();
            scQuery.eq("setmeal_id", s.getId());
            List<SetmealCheckgroup> scList = setmealCheckgroupService.list(scQuery);
            s.setSetmealCheckGroups(scList);
        }
        return new PageResult(page.getTotal(), page.getRecords());
    }

    @GetMapping("/findAll")
    public Result findAll() {
        // 优先读取 Redis 缓存，减少数据库压力
        String cached = redisUtil.get(RedisUtil.SETMEAL_LIST_KEY);
        if (cached != null) {
            return new Result(true, MessageConstant.QUERY_SETMEALLIST_SUCCESS,
                    JSON.parseArray(cached, Setmeal.class));
        }
        List<Setmeal> list = setmealService.list();
        // 写入缓存，有效期 30 分钟
        redisUtil.set(RedisUtil.SETMEAL_LIST_KEY, JSON.toJSONString(list), 30 * 60);
        return new Result(true, MessageConstant.QUERY_SETMEALLIST_SUCCESS, list);
    }

    @DeleteMapping("/deleteInfoById/{id}")
    @Transactional
    public Result deleteInfoById(@PathVariable Integer id) {
        try {
            // 删除套餐与检查组的关联关系
            QueryWrapper<SetmealCheckgroup> scQuery = new QueryWrapper<>();
            scQuery.eq("setmeal_id", id);
            setmealCheckgroupService.remove(scQuery);
            // 删除套餐基本信息
            setmealService.removeById(id);
            // 刷新 Redis 缓存
            redisUtil.delete(RedisUtil.SETMEAL_LIST_KEY);
            return new Result(true, "删除套餐成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除套餐失败");
        }
    }

    @PostMapping("/edit")
    @Transactional
    public Result edit(@RequestBody Setmeal setmeal) {
        try {
            // 更新套餐基本信息
            setmealService.updateById(setmeal);
            // 删除旧的关联关系
            QueryWrapper<SetmealCheckgroup> scQuery = new QueryWrapper<>();
            scQuery.eq("setmeal_id", setmeal.getId());
            setmealCheckgroupService.remove(scQuery);
            // 保存新的关联关系
            saveSetmealCheckgroup(setmeal);
            // 刷新 Redis 缓存
            redisUtil.delete(RedisUtil.SETMEAL_LIST_KEY);
            return new Result(true, "编辑套餐成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "编辑套餐失败");
        }
    }

    @GetMapping("/findById/{id}")
    public Result findById(@PathVariable Integer id) {
        Setmeal setmeal = setmealService.getById(id);
        // 查询关联的检查组
        QueryWrapper<SetmealCheckgroup> scQuery = new QueryWrapper<>();
        scQuery.eq("setmeal_id", id);
        List<SetmealCheckgroup> scList = setmealCheckgroupService.list(scQuery);
        setmeal.setSetmealCheckGroups(scList);
        return new Result(true, MessageConstant.QUERY_SETMEAL_SUCCESS, setmeal);
    }

    /**
     * 保存套餐与检查组的关联关系
     */
    private void saveSetmealCheckgroup(Setmeal setmeal) {
        List<Integer> checkgroupIds = setmeal.getCheckgroupIds();
        if (checkgroupIds != null && !checkgroupIds.isEmpty()) {
            for (Integer checkgroupId : checkgroupIds) {
                SetmealCheckgroup sc = new SetmealCheckgroup();
                sc.setSetmealId(setmeal.getId());
                sc.setCheckgroupId(checkgroupId);
                setmealCheckgroupService.save(sc);
            }
        }
    }
}
