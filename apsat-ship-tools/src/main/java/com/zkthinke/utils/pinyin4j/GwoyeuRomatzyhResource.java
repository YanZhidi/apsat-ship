package com.zkthinke.utils.pinyin4j;


import com.hp.hpl.sparta.Document;
import com.hp.hpl.sparta.ParseException;
import com.hp.hpl.sparta.Parser;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A class contains resource that translates from Hanyu Pinyin to Gwoyeu
 * Romatzyh
 * 
 * @author Li Min (xmlerlimin@gmail.com)
 * 
 */
class GwoyeuRomatzyhResource {
  /**
   * A DOM model contains Hanyu Pinyin to Gwoyeu Romatzyh mapping
   */
  private Document pinyinToGwoyeuMappingDoc;

  /**
   * @param pinyinToGwoyeuMappingDoc
   *            The pinyinToGwoyeuMappingDoc to set.
   */
  private void setPinyinToGwoyeuMappingDoc(Document pinyinToGwoyeuMappingDoc) {
    this.pinyinToGwoyeuMappingDoc = pinyinToGwoyeuMappingDoc;
  }

  /**
   * @return Returns the pinyinToGwoyeuMappingDoc.
   */
  Document getPinyinToGwoyeuMappingDoc() {
    return pinyinToGwoyeuMappingDoc;
  }

  /**
   * Private constructor as part of the singleton pattern.
   */
  private GwoyeuRomatzyhResource() {
    initializeResource();
  }

  /**
   * Initialiez a DOM contains Hanyu Pinyin to Gwoyeu mapping
   */
  private void initializeResource() {
    try {
      final String mappingFileName = "/pinyindb/pinyin_gwoyeu_mapping.xml";
      final String systemId = "";

      // Parse file to DOM Document
      setPinyinToGwoyeuMappingDoc(Parser.parse(systemId, ResourceHelper
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
  static GwoyeuRomatzyhResource getInstance() {
    return GwoyeuRomatzyhSystemResourceHolder.theInstance;
  }

  /**
   * Singleton implementation helper.
   */
  private static class GwoyeuRomatzyhSystemResourceHolder {
    static final GwoyeuRomatzyhResource theInstance = new GwoyeuRomatzyhResource();
  }
}

