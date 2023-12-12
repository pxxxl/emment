package com.minjer.link;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.minjer.pojo.Sentence;
import com.minjer.utils.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 情感分析模块
 *
 * @author Minjer
 */
public class EmotionModule {
    /**
     * 调用情感分析模块处理消息列表
     * 待修正，通过命令行传目前存在问题
     * 执行逻辑：
     * 1.获取python执行指令
     * 2.执行python脚本，按照传入的消息列表进行分析
     * 3.将分析结果中的情感信息提取出来，返回一个String集合
     *
     * @param list 消息列表（纯字符串）
     * @return 返回一个集合，这个集合含有对应的情感
     */
    public static List<String> handleSentence(List<String> list) {
        // 将传入的消息列表转换为json字符串
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comments", list);
        String jsonString = jsonObject.toJSONString();

        // 调用情感分析模块
        try {
            // 构建 crawler.py 文件的相对路径
            String headDir = Tool.traverseUp(System.getProperty("user.dir"), 1);
            Tool.saveJsonToFile(jsonString, headDir + File.separator + "DeepLearning");
            String pythonScript = "python " + headDir + File.separator + "DeepLearning" + File.separator + "emotion_detect.py -fi " + headDir + File.separator + "DeepLearning" + File.separator + "emotion_temp.json";
//            System.out.println(pythonScript);
            // 调用 python 爬虫
            Process process = Runtime.getRuntime().exec(pythonScript);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            System.out.println("接到的情感分析结果：" + output);
            // 等待子进程执行完成
            int exitCode = process.waitFor();
            System.out.println("退出码" + exitCode);
            if (exitCode == 0) {
                // 解析 JSON 字符串为 JSON 对象
                JSONArray jsonArray = JSON.parseObject(output.toString()).getJSONArray("emotions");

                // 将 JSON 数组转换为 ArrayList
                return jsonArray.toJavaList(String.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 已废弃
     * 调用情感分析模块处理文件
     * 执行逻辑：
     * 1.获取python执行指令
     * 2.执行python脚本，按照传入文件的路径读取并分析
     * 3.将分析结果中的情感信息提取出来，返回一个String集合
     *
     * @param path
     * @return
     */
    public static List<String> handleSentenceInFile(String path) {
        // 调用情感分析模块
        try {
            String currentWorkingDirectory = System.getProperty("user.dir");
            System.out.println("当前运行路径：" + currentWorkingDirectory);
            // 构建 crawler.py 文件的相对路径
            String pythonScript = "python ../DeepLearning/emotion_detect.py -fi " + path;

            // 调用 python 爬虫
            Process process = Runtime.getRuntime().exec(pythonScript);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            // 等待子进程执行完成
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // 解析 JSON 字符串为 JSON 对象
                JSONArray jsonArray = JSON.parseObject(output.toString()).getJSONArray("emotions");

                // 将 JSON 数组转换为 ArrayList
                return jsonArray.toJavaList(String.class);

            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
