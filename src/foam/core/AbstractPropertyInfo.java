/**
 * @license
 * Copyright 2017 The FOAM Authors. All Rights Reserved.
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package foam.core;

import foam.dao.pg.IndexedPreparedStatement;
import foam.nanos.logger.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractPropertyInfo
    implements PropertyInfo
{
  protected ClassInfo parent;

  @Override
  public PropertyInfo setClassInfo(ClassInfo p) {
    parent = p;
    return this;
  }

  @Override
  public ClassInfo getClassInfo() {
    return parent;
  }

  @Override
  public void toJSON(foam.lib.json.Outputter outputter, Object value) {
    outputter.output(value);
  }

  @Override
  public void toCSV(foam.lib.csv.Outputter outputter, Object value) {
    outputter.output(value);
  }

  @Override
  public foam.mlang.Expr partialEval() {
    return this;
  }

  @Override
  public void prepareStatement(IndexedPreparedStatement stmt) throws SQLException {}

  @Override
  public Object f(Object o) {
    return get(o);
  }

  @Override
  public void diff(FObject o1, FObject o2, Map diff, PropertyInfo prop) {
    if ( ! prop.f(o1).equals(prop.f(o2)) ) {
      diff.put(prop.getName(), prop.f(o2));
    }
  }

  public void setFromString(Object obj, String value) {
    this.set(obj, fromString(value));
  }

  @Override
  public Object fromXML(X x, XMLStreamReader reader) {
    // Moves reader to characters state in order for value reading for various data types (date, boolean, short ...)
    try {
      reader.next();
    } catch (XMLStreamException ex) {
      Logger logger = (Logger) x.get("logger");
      logger.error("Premature end of XML file");
    }
    return "";
  }

  @Override
  public void toXML(FObject obj, Document doc, Element objElement) {
    Object value = this.f(obj);
    if ( value != null && value != "" ) {
      Element prop = doc.createElement(this.getName());
      prop.appendChild(doc.createTextNode(value.toString()));
      objElement.appendChild(prop);
    }
  }

  @Override
  public void setStatementValue(IndexedPreparedStatement stmt, FObject o) throws java.sql.SQLException {
    stmt.setObject(this.get(o));
  }

  @Override
  public void setFromResultSet(java.sql.ResultSet resultSet, int index, FObject o) throws java.sql.SQLException{
    this.set(o, resultSet.getObject(index));
  }

  public String toString() {
    // TODO: generate static string in generated instances instead to avoid creating garbage.
    return parent.getId() + "." + getName();
  }

  @Override
  public void cloneProperty(FObject source, FObject dest) {
    set(dest, foam.util.SafetyUtil.deepClone(get(source)));
  }

  @Override
  public void validate(FObject obj) throws IllegalStateException {}

  @Override
  public void updateDigest(FObject obj, MessageDigest md) {}

  @Override
  public void updateSignature(FObject obj, Signature sig) throws SignatureException {}

  protected byte[] nameAsByteArray_ = null;

  @Override
  public byte[] getNameAsByteArray() {
    if ( nameAsByteArray_ == null ) {
      nameAsByteArray_ = getName().getBytes(StandardCharsets.UTF_8);
    }
    return nameAsByteArray_;
  }
}
