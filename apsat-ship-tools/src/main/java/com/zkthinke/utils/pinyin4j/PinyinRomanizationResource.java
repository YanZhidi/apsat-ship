package com.zkthinke.utils.pinyin4j;


import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.ParseException;
import com.hp.hpl.sparta.Parser;

import java.io.FileNotFoundException;
import java.io.IOException;

class PinyinRomanizationResource {
  /**
   * A DOM model contains variable pinyin presentations
   */
  private Document pinyinMappingDoc;

  /**
   * @param pinyinMappingDoc
   *            The pinyinMappingDoc to set.
   */
  private void setPinyinMappingDoc(Document pinyinMappingDoc) {
    this.pinyinMappingDoc = pinyinMappingDoc;
  }

  /**
   * @return Returns the pinyinMappingDoc.
   */
  Document getPinyinMappingDoc() {
    return pinyinMappingDoc;
  }

  /**
   * Private constructor as part of the singleton pattern.
   */
  private PinyinRomanizationResource() {
    initializeResource();
  }

  /**
   * Initialiez a DOM contains variable PinYin representations
   */
  private void initializeResource() {
    try {
      final String mappingFileName = "/pinyindb/pinyin_mapping.xml";
      final String systemId = "";

      // Parse file to DOM Document
      setPinyinMappingDoc(Parser.parse(systemId, ResourceHelper
          .getResourceInputStream(mappingFileName)));

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Singleton factory method.
   * 
   * @return the one and only MySingleton.
   */
  static PinyinRomanizationResource getInstance() {
    return PinyinRomanizationSystemResourceHolder.theInstance;
  }

  /**
   * Singleton implementation helper.
   */
  private static class PinyinRomanizationSystemResourceHolder {
    static final PinyinRomanizationResource theInstance = new PinyinRomanizationResource();
  }
}

