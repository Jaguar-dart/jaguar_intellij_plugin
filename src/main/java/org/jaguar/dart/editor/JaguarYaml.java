package org.jaguar.dart.editor;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class JaguarYaml {
  @NotNull
  private final HashSet<String> apis = new HashSet<>();

  @NotNull
  private final HashSet<String> serializers = new HashSet<>();

  @NotNull
  private final HashSet<String> validators = new HashSet<>();

  @NotNull
  private final HashSet<String> beans = new HashSet<>();

  @NotNull
  public HashSet<String> getApis() {
    return apis;
  }

  public void setApis(@NotNull HashSet<String> apis) {
    this.apis.clear();
    this.apis.addAll(apis);
  }

  @NotNull
  public HashSet<String> getSerializers() {
    return serializers;
  }

  public void setSerializers(@NotNull HashSet<String> serializer) {
    this.serializers.clear();
    this.serializers.addAll(serializer);
  }

  @NotNull
  public HashSet<String> getValidators() {
    return validators;
  }

  public void setValidators(@NotNull HashSet<String> validators) {
    this.validators.clear();
    this.validators.addAll(validators);
  }

  @NotNull
  public HashSet<String> getBeans() {
    return beans;
  }

  public void setBeans(@NotNull HashSet<String> beans) {
    this.beans.clear();
    this.beans.addAll(beans);
  }

  public void addApi(@NotNull String api) {
    apis.add(api);
  }

  public void removeApi(@NotNull String api) {
    apis.remove(api);
  }

  public void removeApis(@NotNull List<String> api) {
    apis.removeAll(api);
  }

  public void removeSerializer(@NotNull String serializer) {
    serializers.remove(serializer);
  }

  public void removeSerializers(@NotNull List<String> serializer) {
    serializers.removeAll(serializer);
  }

  public void removeValidator(@NotNull String validator) {
    validators.remove(validator);
  }

  public void removeValidators(@NotNull List<String> validator) {
    validators.removeAll(validator);
  }

  public void removeBean(@NotNull String bean) {
    beans.remove(bean);
  }

  public void removeBeans(@NotNull List<String> bean) {
    beans.removeAll(bean);
  }

  public void addSerializer(@NotNull String serializer) {
    serializers.add(serializer);
  }

  public void addValidator(@NotNull String validator) {
    validators.add(validator);
  }

  public void addBean(@NotNull String bean) {
    beans.add(bean);
  }

  public void copy(JaguarYaml other) {
    setApis(other.apis);
    setSerializers(other.serializers);
    setValidators(other.validators);
    setBeans(other.beans);
  }

  @NotNull
  public static JaguarYaml read(String text) {
    YamlReader reader = new YamlReader(text);

    @NotNull JaguarYaml yaml = new JaguarYaml();

    try {
      yaml = reader.read(JaguarYaml.class);
    } catch (YamlException ex) {
      //Do nothing
    }

    return yaml;
  }

  @NotNull
  public static JaguarYaml read(InputStream stream) {
    String text = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
    return read(text);
  }

  @NotNull
  public static String write(@NotNull JaguarYaml yaml) {
    StringWriter strWriter = new StringWriter();
    YamlWriter writer = new YamlWriter(strWriter);

    @NotNull String ret = "";
    try {
      writer.write(yaml);
      writer.close();
      ret = strWriter.toString();
    } catch (Exception ex) {
      //Do nothing
    }

    return ret;
  }

  public static void write(@NotNull JaguarYaml yaml, OutputStream stream) {
    String text = write(yaml);

    OutputStreamWriter writer = new OutputStreamWriter(stream);

    try {
      writer.write(text);
    } catch (Exception ex) {
      //TODO notification
    }

    try {
      writer.close();
    } catch (Exception ex) {
      //Do nothing
    }
  }
}
