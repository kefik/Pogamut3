package cz.cuni.pogamut.ut2004.levelgeom.xml;
/**
 * From UShock:
    enum EPolyFlags {
		// Regular in-game flags.
		PF_Invisible		( 0x00000001,	// Poly is invisible.
		PF_Masked			( 0x00000002,	// Poly should be drawn masked.
		PF_Translucent	 	( 0x00000004,	// Poly is transparent.
		PF_NotSolid			( 0x00000008,	// Poly is not solid, doesn't block.
		PF_Environment   	( 0x00000010,	// Poly should be drawn environment mapped.
		PF_ForceViewZone	( 0x00000010,	// Force current iViewZone in OccludeBSP (reuse Environment flag)
		PF_Semisolid	  	( 0x00000020,	// Poly is semi-solid ( collision solid, Csg nonsolid.
		PF_Modulated 		( 0x00000040,	// Modulation transparency.
		PF_FakeBackdrop		( 0x00000080,	// Poly looks exactly like backdrop.
		PF_TwoSided			( 0x00000100,	// Poly is visible from both sides.
		PF_AutoUPan		 	( 0x00000200,	// Automatically pans in U direction.
		PF_AutoVPan 		( 0x00000400,	// Automatically pans in V direction.
		PF_NoSmooth			( 0x00000800,	// Don't smooth textures.
		PF_BigWavy 			( 0x00001000,	// Poly has a big wavy pattern in it.
		PF_SpecialPoly		( 0x00001000,	// Game-specific poly-level render control (reuse BigWavy flag)
		PF_SmallWavy		( 0x00002000,	// Small wavy pattern (for water/enviro reflection).
		PF_Flat				( 0x00004000,	// Flat surface.
		PF_LowShadowDetail	( 0x00008000,	// Low detaul shadows.
		PF_NoMerge			( 0x00010000,	// Don't merge poly's nodes before lighting when rendering.
		PF_CloudWavy		( 0x00020000,	// Polygon appears wavy like clouds.
		PF_DirtyShadows		( 0x00040000,	// Dirty shadows.
		PF_BrightCorners	( 0x00080000,	// Brighten convex corners.
		PF_SpecialLit		( 0x00100000,	// Only speciallit lights apply to this poly.
		PF_Gouraud			( 0x00200000,	// Gouraud shaded.
		PF_NoBoundRejection ( 0x00200000,	// Disable bound rejection in OccludeBSP (reuse Gourard flag)
		PF_Unlit			( 0x00400000,	// Unlit.
		PF_HighShadowDetail	( 0x00800000,	// High detail shadows.
		PF_Portal			( 0x04000000,	// Portal between iZones.
		PF_Mirrored			( 0x08000000,	// Reflective surface.
	
		// Editor flags.
		PF_Memorized     	( 0x01000000,	// Editor: Poly is remembered.
		PF_Selected      	( 0x02000000,	// Editor: Poly is selected.
		PF_Highlighted      ( 0x10000000,	// Editor: Poly is highlighted.   
		PF_FlatShaded		( 0x40000000,	// FPoly has been split by SplitPolyWithPlane.   
	
		// Internal.
		PF_EdProcessed 		( 0x40000000,	// FPoly was already processed in editorBuildUPolys.
		PF_EdCut       		( 0x80000000,	// FPoly has been split by SplitPolyWithPlane.  
		PF_RenderFog		( 0x40000000,	// Render with fogmapping.
		PF_Occlude			( 0x80000000,	// Occludes even if PF_NoOcclude.
		PF_RenderHINT       ( 0x01000000,   // Rendering optimization hINT.
	
		// Combinations of flags.
		PF_NoOcclude		( PF_Masked | PF_Translucent | PF_Invisible | PF_Modulated,
		PF_NoEdit			( PF_Memorized | PF_Selected | PF_EdProcessed | PF_NoMerge | PF_EdCut,
		PF_NoImport			( PF_NoEdit | PF_NoMerge | PF_Memorized | PF_Selected | PF_EdProcessed | PF_EdCut,
		PF_AddLast			( PF_Semisolid | PF_NotSolid,
		PF_NoAddToBSP		( PF_EdCut | PF_EdProcessed | PF_Selected | PF_Memorized,
		PF_NoShadows		( PF_Unlit | PF_Invisible | PF_Environment | PF_FakeBackdrop,
		PF_Transient		( PF_Highlighted,
    };
 *
 * 
 * @author Jimmy
 *
 */
public enum BspNodeFlag {
	
	PF_Invisible		( 0x00000001),	// Poly is invisible.
	PF_Masked			( 0x00000002),	// Poly should be drawn masked.
	PF_Translucent	 	( 0x00000004),	// Poly is transparent.
	PF_NotSolid			( 0x00000008),	// Poly is not solid), doesn't block.
	PF_Environment   	( 0x00000010),	// Poly should be drawn environment mapped.
	PF_ForceViewZone	( 0x00000010),	// Force current iViewZone in OccludeBSP (reuse Environment flag)
	PF_Semisolid	  	( 0x00000020),	// Poly is semi-solid ( collision solid), Csg nonsolid.
	PF_Modulated 		( 0x00000040),	// Modulation transparency.
	PF_FakeBackdrop		( 0x00000080),	// Poly looks exactly like backdrop.
	PF_TwoSided			( 0x00000100),	// Poly is visible from both sides.
	PF_AutoUPan		 	( 0x00000200),	// Automatically pans in U direction.
	PF_AutoVPan 		( 0x00000400),	// Automatically pans in V direction.
	PF_NoSmooth			( 0x00000800),	// Don't smooth textures.
	PF_BigWavy 			( 0x00001000),	// Poly has a big wavy pattern in it.
	PF_SpecialPoly		( 0x00001000),	// Game-specific poly-level render control (reuse BigWavy flag)
	PF_SmallWavy		( 0x00002000),	// Small wavy pattern (for water/enviro reflection).
	PF_Flat				( 0x00004000),	// Flat surface.
	PF_LowShadowDetail	( 0x00008000),	// Low detaul shadows.
	PF_NoMerge			( 0x00010000),	// Don't merge poly's nodes before lighting when rendering.
	PF_CloudWavy		( 0x00020000),	// Polygon appears wavy like clouds.
	PF_DirtyShadows		( 0x00040000),	// Dirty shadows.
	PF_BrightCorners	( 0x00080000),	// Brighten convex corners.
	PF_SpecialLit		( 0x00100000),	// Only speciallit lights apply to this poly.
	PF_Gouraud			( 0x00200000),	// Gouraud shaded.
	PF_NoBoundRejection ( 0x00200000),	// Disable bound rejection in OccludeBSP (reuse Gourard flag)
	PF_Unlit			( 0x00400000),	// Unlit.
	PF_HighShadowDetail	( 0x00800000),	// High detail shadows.
	PF_Portal			( 0x04000000),	// Portal between iZones.
	PF_Mirrored			( 0x08000000),	// Reflective surface.

	// Editor flags.
	PF_Memorized     	( 0x01000000),	// Editor: Poly is remembered.
	PF_Selected      	( 0x02000000),	// Editor: Poly is selected.
	PF_Highlighted      ( 0x10000000),	// Editor: Poly is highlighted.   
	PF_FlatShaded		( 0x40000000),	// FPoly has been split by SplitPolyWithPlane.   

	// Internal.
	PF_EdProcessed 		( 0x40000000),	// FPoly was already processed in editorBuildUPolys.
	PF_EdCut       		( 0x80000000),	// FPoly has been split by SplitPolyWithPlane.  
	PF_RenderFog		( 0x40000000),	// Render with fogmapping.
	PF_Occlude			( 0x80000000),	// Occludes even if PF_NoOcclude.
	PF_RenderHINT       ( 0x01000000);   

	// Rendering optimization hINT.
		
// Combinations of flags.
//			PF_NoOcclude		( PF_Masked | PF_Translucent | PF_Invisible | PF_Modulated),
//			PF_NoEdit			( PF_Memorized | PF_Selected | PF_EdProcessed | PF_NoMerge | PF_EdCut),
//			PF_NoImport			( PF_NoEdit | PF_NoMerge | PF_Memorized | PF_Selected | PF_EdProcessed | PF_EdCut),
//			PF_AddLast			( PF_Semisolid | PF_NotSolid),
//			PF_NoAddToBSP		( PF_EdCut | PF_EdProcessed | PF_Selected | PF_Memorized),
//			PF_NoShadows		( PF_Unlit | PF_Invisible | PF_Environment | PF_FakeBackdrop),
//			PF_Transient		( PF_Highlighted),
			
	private int flag;
	
	private BspNodeFlag(int flag) {
		this.flag = flag;
	}

}
