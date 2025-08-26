package com.axonivy.connector.langchain.test;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.langchain.AiBrain;
import com.axonivy.connector.langchain.schema.OpenAiSchemaModel;
import com.axonivy.connector.langchain.schema.SchemaLoader;
import com.fasterxml.jackson.databind.JsonNode;

import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.environment.IvyTest;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.internal.Json;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ChatRequest.Builder;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonRawSchema;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.internal.chat.JsonSchema;

@IvyTest
public class ProcessSchemaGenTest {

  @BeforeEach
  void logs() {
    var langChain = Logger.getLogger(dev.langchain4j.http.client.log.LoggingHttpClient.class);
    langChain.setLevel(Level.DEBUG);
  }

  @Test
  void askElon() {
    var model = new AiBrain().grokModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("simple.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askElon_grok3inlineLite() {
    var model = new AiBrain().grokModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-inline-lite.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askElon_grok4remote() {
    var model = new AiBrain().grokModel()
        .modelName("grok-4-0709")
        .timeout(Duration.ofMinutes(4)) // be patient!
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-remote.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askElon_grok2remote() {
    var model = new AiBrain().grokModel()
        .modelName("grok-2-vision-1212")
        .timeout(Duration.ofMinutes(4)) // be patient!
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-remote.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  @Disabled("runs into timeout")
  void askElon_grok3mini_inlineFull() {
    var model = new AiBrain().grokModel()
        .timeout(Duration.ofMinutes(4)) // be patient!
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-inline-full.json");
    var writeMailProcess = processGeneration("write a soap process, that returns product names of our ERP database").build();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askOpenAi() {
    var model = new AiBrain().buildModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .modelName(OpenAiChatModelName.GPT_4_1_MINI)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("simple.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);

    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askOpenAi_gpt41mini_inlineLite() {
    var model = new AiBrain().buildModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .modelName(OpenAiChatModelName.GPT_4_1_MINI)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-inline-lite.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askOpenAi_gpt41mini_inlineFull() {
    var model = strictSchemaOpenAi();

    var lc4jSchema = processSchema("proc-inline-full.json");
    var writeMailProcess = processGeneration("write a soap process, that returns product names of our ERP database")
        .build();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  @Test
  void askOpenAi_native120api_gpt41mini_inlineFull() {
    var model = strictSchemaOpenAi();
    // TODO emphasize correctness; play with editing
    var json = generateProcess(model, "write a soap process, that returns product names of our ERP database");
    System.out.println(json.toPrettyString());
  }

  @Test
  void askOpenAi_native120api_gpt41mini_inlineFull_mail() {
    var model = strictSchemaOpenAi();
    // TODO emphasize correctness; play with editing
    var json = generateProcess(model,
        "add an email element, telling rolf@axonivy.com that we got the lead!");
    System.out.println(json.toPrettyString());
  }

  @Test
  void askOpenAi_native120api_gpt41mini_inlineFull_multiElement() throws IOException {

    var model = strictSchemaOpenAi();
    // TODO emphasize correctness; play with editing
    var json = generateProcess(model,
        """
          start the process based on a signal, referencing a slack-message from a new customer
          add an email element, telling rolf@axonivy.com that we got a new lead!
          use an alternative gateway, if the predicted license cost is higher than 100K dollars, create a task for marcel with high priority otherwise simply end the process.
          """);
    System.out.println(json.toPrettyString());

    String where = IProcessModelVersion.current().getProjectDirectory();
    System.out.println(where);
    Path testProject = Path.of(where);// .getParent().resolve("langchain-service");
    Path leadExample = testProject.resolve("Processes").resolve("Lead.p.json");
    Files.writeString(leadExample, json.toPrettyString(), StandardOpenOption.WRITE);
  }

  private OpenAiChatModel strictSchemaOpenAi() {
    return new AiBrain().buildModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .modelName(OpenAiChatModelName.GPT_4_1_MINI)
        .strictJsonSchema(false)
        .logRequests(true)
        .logResponses(true)
        .build();
  }

  private JsonNode generateProcess(OpenAiChatModel model, String instruction) {
    var format = nativeResponsePR("proc-inline-full.json");
    var writeMailProcess = processGeneration(instruction)
        .responseFormat(format)
        .build();

    var out = model.chat(writeMailProcess);
    String rawJson = out.aiMessage().text();
    return Json.fromJson(rawJson, JsonNode.class);
  }

  @Test
  @Disabled("explicitly rejected by gpt4.1mini")
  void askOpenAi_remoteRefs() {
    var model = new AiBrain().buildModel()
        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
        .modelName(OpenAiChatModelName.GPT_4_1_MINI)
        .strictJsonSchema(true)
        .logRequests(true)
        .logResponses(true)
        .build();

    var lc4jSchema = processSchema("proc-remote.json");
    var writeMailProcess = processGeneration();
    var ai = new OpenAiSchemaModel(model);
    var generatedProcess = ai.chat(writeMailProcess, lc4jSchema);

    System.out.println(generatedProcess.toPrettyString());
  }

  private ChatRequest processGeneration() {
    return processGenerationBuilder().build();
  }

  private Builder processGenerationBuilder() {
    return processGeneration("add an email element, telling rolf@axonivy.com that we got the lead!");
  }

  private Builder processGeneration(String msg) {
    var processHints = new SystemMessage("""
      omit as many defaults as possible, but at any rate produce the required values.
      Generate the 'data' as java qualified name.
      For element ID's create unique instances, starting from f1.
      Draw elements as graph.
      Do not set any visual attributes on element, except the position 'at'.
      Set the root process 'id' out of 16 random uppercase letters or numbers.
      """);

    return ChatRequest.builder()
        .messages(processHints, new UserMessage(msg));
  }

  private ResponseFormat nativeResponsePR(String resource) {
    var jsonNode = SchemaLoader.readSchema(resource);
    JsonRawSchema nativeSchema = JsonRawSchema.from(jsonNode.toString());
    var jsonSchema = new dev.langchain4j.model.chat.request.json.JsonSchema.Builder()
        .name(Strings.CS.removeEnd(resource, ".json"))
        .rootElement(nativeSchema)
        .build();
    return ResponseFormat.builder()
        .type(ResponseFormatType.JSON)
        .jsonSchema(jsonSchema)
        .build();
  }

  private JsonSchema processSchema(String resource) {
    var jsonSchema = SchemaLoader.readSchema(resource);// SchemaLoader.readSchema("process.json");
    var maap = jsonSchema.properties().stream()
        .collect(Collectors.toMap(Entry::getKey, et -> (Object) et.getValue()));
    return dev.langchain4j.model.openai.internal.chat.JsonSchema.builder()
        .name(StringUtils.substringBefore(resource, "."))
        .strict(false)
        .schema(maap)
        .build();
  }

}
