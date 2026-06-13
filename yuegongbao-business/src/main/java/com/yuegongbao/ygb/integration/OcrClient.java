package com.yuegongbao.ygb.integration;

import com.yuegongbao.ygb.domain.vo.YgbOcrExtractResponse;

/**
 * OCR 识别接口。
 *
 * @author yuegongbao
 */
public interface OcrClient
{
    YgbOcrExtractResponse extractContract(String contractFileUrl);
}
