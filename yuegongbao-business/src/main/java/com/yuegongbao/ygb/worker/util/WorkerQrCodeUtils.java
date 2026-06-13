package com.yuegongbao.ygb.worker.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.yuegongbao.common.exception.ServiceException;
import java.util.HashMap;
import java.util.Map;

public final class WorkerQrCodeUtils
{
    private WorkerQrCodeUtils()
    {
    }

    public static String toPngDataUrl(String text, int size)
    {
        try
        {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF111111 : 0xFFFFFFFF);
                }
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
        catch (Exception e)
        {
            throw new ServiceException("生成电子工牌二维码失败。").setDetailMessage(e.getMessage());
        }
    }
}
