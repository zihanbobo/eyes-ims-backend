package com.tongren.controller;

import com.github.pagehelper.PageInfo;
import com.tongren.bean.CommonResult;
import com.tongren.bean.Constant;
import com.tongren.bean.Identity;
import com.tongren.bean.PageResult;
import com.tongren.bean.rolecheck.RequiredRoles;
import com.tongren.bean.user.UserExtend;
import com.tongren.pojo.User;
import com.tongren.service.PropertyService;
import com.tongren.service.RedisService;
import com.tongren.service.UserService;
import com.tongren.util.FileUtil;
import com.tongren.util.MD5Util;
import com.tongren.util.Validator;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController
 */
@Controller
@RequestMapping("user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private RedisService redisService;


    /**
     * 添加员工
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addUser(@RequestBody Map<String, Object> params) {

        String name = (String) params.get(Constant.NAME);
        String username = (String) params.get(Constant.USERNAME);
        String role = (String) params.get(Constant.ROLE);

        User user = new User();

        if (Validator.checkEmpty(name) || Validator.checkEmpty(username) || Validator.checkEmpty(role)) {
            return CommonResult.failure("添加失败，信息不完整");
        } else {
            user.setName(name);
            user.setUsername(username);
            user.setRole(role);
        }

        if (this.userService.isExist(username)) {
            return CommonResult.failure("该用户名已被注册");
        }

        try {
            user.setPassword(MD5Util.generate(Constant.DEFAULT_PASSWORD));
            user.setAvatar("avatar_default.png"); // 默认头像
            this.userService.save(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("添加失败，md5生成错误");
        }

        return CommonResult.success("添加成功");
    }


    /**
     * 修改别的用户的信息
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateOtherUser(@RequestBody Map<String, Object> params) {

        Integer userId = (Integer) params.get("userId");
        // 修改别的用户的时候不能修改name和phone
        String name = (String) params.get(Constant.NAME);
        String role = (String) params.get(Constant.ROLE);

        // 未修改的user
        User user = this.userService.queryById(userId);

        if (!Validator.checkEmpty(name)) {
            user.setName(name);
        }

        // role
        if (!Validator.checkEmpty(role)) {
            user.setRole(role);
        }

        this.userService.update(user);

        return CommonResult.success("修改成功");
    }


    /**
     * 查询用户信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryById(@PathVariable("userId") Integer userId) {

        User user = this.userService.queryById(userId);
        if (user == null) {
            return CommonResult.failure("用户不存在");
        }

        return CommonResult.success("查询成功", user);
    }


    /**
     * 删除用户
     * role改为已删除，username加上_deleted的后缀
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"系统管理员"})
    public CommonResult deleteById(@PathVariable("userId") Integer userId) {

        User user = this.userService.queryById(userId);
        if (user == null) {
            return CommonResult.failure("用户不存在");
        }

        // this.userService.deleteById(userId);
        this.userService.delete(user);

        logger.info("删除用户：{}", user.getName());

        return CommonResult.success("删除成功");
    }


    /**
     * 用户自己修改自己
     *
     * @param userId
     * @param params
     * @return
     */
    @RequestMapping(value = "{userId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateById(@PathVariable("userId") Integer userId, @RequestBody Map<String, Object> params) {

        // 自己可以修改自己的name和phone
        String name = (String) params.get(Constant.NAME);

        // 未修改的user
        User user = this.userService.queryById(userId);

        if (!Validator.checkEmpty(name)) {
            user.setName(name);
        }

        this.userService.update(user);
        return CommonResult.success("修改成功");
    }



    /**
     * 条件分页查询用户
     * 会员member、职员employee
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryUsers(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String role = (String) params.get(Constant.ROLE);
        String username = (String) params.get(Constant.USERNAME);
        String name = (String) params.get(Constant.NAME);

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<User> userList = this.userService.queryUserList(pageNow, pageSize, role, username, name, identity);
        PageResult pageResult = new PageResult(new PageInfo<>(userList));

        logger.info("pageNow: {}, pageSize: {}, role: {}, phone: {}, name: {}", pageNow, pageSize, role, username, name);

        return CommonResult.success("查询成功", pageResult);
    }


    /**
     * 修改密码
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "password/{userId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult changePassword(@RequestBody Map<String, Object> params, @PathVariable("userId") Integer
            userId) {

        String oldPassword = (String) params.get("oldPassword");
        String newPassword = (String) params.get("newPassword");

        User user = this.userService.queryById(userId);

        // 找回密码的时候没有oldPassword
        if (!Validator.checkEmpty(oldPassword)) {
            String oldPasswordMD5;
            try {
                oldPasswordMD5 = MD5Util.generate(oldPassword);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return CommonResult.failure("md5加密失败！");
            }

            if (!oldPasswordMD5.equals(user.getPassword())) {
                return CommonResult.failure("修改失败，原密码输入错误");
            }
        }

        String newPasswordMD5;
        try {
            newPasswordMD5 = MD5Util.generate(newPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return CommonResult.failure("md5加密失败");
        }

        user.setPassword(newPasswordMD5);
        this.userService.update(user);

        return CommonResult.success("密码修改成功");
    }


    /**
     * 修改用户头像
     *
     * @param file
     * @param id
     * @return
     */
    @RequestMapping(value = "avatar", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult uploadAvatar(@RequestParam("file") MultipartFile file, Integer id) {

        User user = this.userService.queryById(id);
        if (user == null) {
            return CommonResult.failure("上传失败，用户不存在");
        }

        String fileName;
        if (!file.isEmpty()) {

            fileName = id + "." + FileUtil.getExtensionName(file.getOriginalFilename());

            try {
                Streams.copy(file.getInputStream(), new FileOutputStream(Constant.FILE_PATH + "avatar/" +
                        fileName), true);
            } catch (IOException e) {
                e.printStackTrace();
                return CommonResult.failure("头像上传失败");
            }

            user.setAvatar(fileName);
            this.userService.update(user);
        } else {
            return CommonResult.failure("头像上传失败");
        }

        return CommonResult.success("头像上传成功", "/avatar/" + fileName);
    }
}
