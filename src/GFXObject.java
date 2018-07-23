
public class GFXObject
	{
		private int realWidthInNormal, realHeightInNormal, realWidthInSafe, realHeightInSafe;
		private String normal, safemode;
		
		public GFXObject (String normalUrl, int rwidthNormal, int rheightNormal, String safeUrl, int rwidthSafe, int rheightSafe)
		{
			normal = normalUrl;
			safemode = safeUrl;
			realWidthInNormal = rwidthNormal;
			realHeightInNormal = rheightNormal;
			realWidthInSafe = rwidthSafe;
			realHeightInSafe = rheightSafe;
		}
	}