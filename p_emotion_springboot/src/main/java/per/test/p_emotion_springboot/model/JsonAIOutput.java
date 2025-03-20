package per.test.p_emotion_springboot.model;
import ai.AiReport.AIOutput;
public class JsonAIOutput {
    private String report;

    public JsonAIOutput(AIOutput aiOutput) {
        this.report = aiOutput.getReport(); // 只取可序列化的字段
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
