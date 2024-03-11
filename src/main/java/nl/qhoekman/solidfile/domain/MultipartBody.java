package nl.qhoekman.solidfile.domain;

import org.jboss.resteasy.reactive.RestForm;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MultipartBody {

  @RestForm("name")
  @XmlMimeType((MediaType.TEXT_PLAIN))
  String name;

  @RestForm("file")
  @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
  byte[] bytes;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }
}