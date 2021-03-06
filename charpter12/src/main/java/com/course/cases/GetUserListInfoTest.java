package com.course.cases;

import com.course.config.TestConfig;
import com.course.module.GetUserList;
import com.course.module.User;
import com.course.utils.DatabaseUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class GetUserListInfoTest {

    @Test(dependsOnGroups = "loginTrue",description = "获取用户信息列表接口测试")
    public void getUserListInfo() throws IOException {

        //获取用来执行sql的SqlSession
        SqlSession session = DatabaseUtil.getSqlSession();

        //查询自己数据库中的测试用例数据
        GetUserList getUserList = session.selectOne("getUserInfoListCase",5);

        //输出查询到的结果和将要访问的路由
        System.out.println("获取用户列表测试用例数据" + getUserList.toString());
        System.out.println("获取用户列表路由：" + TestConfig.getUserListUrl);

        //实际结果（获取请求中接口返回的数据 参数 用例数据&接口路由地址）
        String requestResult = getJSONArray(getUserList,TestConfig.getUserListUrl);

        //查询预期结果，并将获取到的user列表循环输出
        List<User> userList = session.selectList("getUserInfoList",getUserList);
        for (User u:userList) {
            System.out.println("获取到的用户信息：" + u);
        }

        JSONArray jsonArrayResult = new JSONArray(requestResult);
        JSONArray jsonArrayExpected = new JSONArray(userList);
        System.out.println("预期结果：" + jsonArrayExpected.toString());
        System.out.println("实际结果：" + jsonArrayResult.toString());

        //对比预期结果与实际结果的字符串值
        if (jsonArrayExpected.toString().equals(jsonArrayResult.toString())){
            System.out.println("用户列表信息接口对比成功！");
        }else {
            throw new AssertionError();
        }


//        System.out.println("sessionList数据" + sessionList);
//        System.out.println("getArray数据" + getArray);
//
//        for (int i = 0; i < userList.size(); i++) {
//            JSONObject expected = (JSONObject) sessionList.get(i);
//            JSONObject result = (JSONObject) getArray.get(i);
//            Assert.assertEquals(expected,result);
//        }

    }

    private String getJSONArray(GetUserList userList,String url) throws IOException {

        //新建JSON对象，并设置值
        JSONObject json = new JSONObject();
        json.put("id",userList.getId());
        json.put("userName",userList.getUserName());
        json.put("age",userList.getAge());
        json.put("sex",userList.getSex());
        json.put("permission",userList.getPermission());

        //新建post请求，并将参数路由加进去
        HttpPost post = new HttpPost(url);
        //
        StringEntity entit = new StringEntity(json.toString(),"utf-8");
        post.setEntity(entit);
        post.setHeader("content-type","application/json");

        HttpResponse response = TestConfig.client.execute(post);
        String result = EntityUtils.toString(response.getEntity(),"utf-8");
        System.out.println("请求GetUserListInfo接口获取到的结果：" + result);
        return result;
    }

}
