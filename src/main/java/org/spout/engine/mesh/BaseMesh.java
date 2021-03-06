/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.mesh;

import java.util.ArrayList;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;
import org.spout.api.model.Mesh;
import org.spout.api.model.MeshFace;
import org.spout.api.model.Vertex;
import org.spout.api.render.RenderEffect;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;
import org.spout.api.resource.Resource;
import org.spout.engine.renderer.BatchVertexRenderer;


public class BaseMesh extends Resource implements Mesh, Iterable<MeshFace> {
	ArrayList<MeshFace> faces;
	ArrayList<RenderEffect> effects = new ArrayList<RenderEffect>();
	boolean dirty = false;

	Renderer renderer;
	
	
	public BaseMesh(){
		faces = new ArrayList<MeshFace>();
		
	}
	
	public BaseMesh(ArrayList<MeshFace> faces){
		this.faces = faces;
	}
	
	@Override
	public void addRenderEffect(RenderEffect effect) {
		effects.add(effect);
	}

	@Override
	public void removeRenderEffect(RenderEffect effect) {
		effects.remove(effect);
	}

	@Override
	public RenderEffect[] getEffects() {
		return effects.toArray(new RenderEffect[effects.size()]);
	}

	private void preBatch(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.preBatch(batcher);
		}
	}

	private void postBatch(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.postBatch(batcher);
		}
	}

	protected void batch(Renderer batcher) {
		for (MeshFace face : faces) {
			for(Vertex vert : face){
				batcher.addTexCoord(vert.texCoord0);
				batcher.addNormal(vert.normal);
				batcher.addVertex(vert.position);
				batcher.addColor(vert.color);
			}
		}
	}

	private void preRender(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.preDraw(batcher);
		}
	}

	private void postRender(Renderer batcher) {
		for (RenderEffect effect : effects) {
			effect.postDraw(batcher);
		}
	}


	public void batch(){
		if(renderer == null) renderer = BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		preBatch(renderer);
		this.batch(renderer);
		postBatch(renderer);
	}
	
	public void render(RenderMaterial material){
		if(renderer == null) throw new IllegalStateException("Cannot render without batching first!");
		
		preRender(renderer);
		renderer.render(material);
		postRender(renderer);	
	}
	
	

	@Override
	public Iterator<MeshFace> iterator() {
		return faces.iterator();
	}
}
